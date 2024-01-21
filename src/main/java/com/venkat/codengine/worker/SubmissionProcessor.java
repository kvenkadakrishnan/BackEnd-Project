package com.venkat.codengine.worker;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.venkat.codengine.Utils.StorageService;
import com.venkat.codengine.constants.StatusMessage;
import com.venkat.codengine.constants.SubmissionStatus;
import com.venkat.codengine.dto.QueueMessageDTO;
import com.venkat.codengine.entity.DriverCode;
import com.venkat.codengine.entity.Problems;
import com.venkat.codengine.entity.SubmissionResult;
import com.venkat.codengine.entity.Submissions;
import com.venkat.codengine.repository.DriverCodeRepo;
import com.venkat.codengine.repository.ProblemsRepo;
import com.venkat.codengine.repository.SubmissionResultRepo;
import com.venkat.codengine.repository.SubmissionsRepo;
import com.venkat.codengine.worker.codeexecutor.CodeExecutorFactory;
import com.venkat.codengine.worker.dto.ExecutionResult;

@Component
public class SubmissionProcessor {
	private MessageQueue messageQueue;
	private SubmissionsRepo submissionsRepo;
	private ProblemsRepo problemsRepo;
	private DriverCodeRepo driverCodeRepo;
	private SubmissionResultRepo submissionResultRepo;
	public SubmissionProcessor(MessageQueue messageQueue,SubmissionsRepo submissionsRepo,ProblemsRepo problemsRepo, DriverCodeRepo driverCodeRepo, SubmissionResultRepo submissionResultRepo) {
		this.messageQueue = messageQueue;
		this.problemsRepo = problemsRepo;
		this.submissionsRepo = submissionsRepo;
		this.driverCodeRepo = driverCodeRepo;
		this.submissionResultRepo = submissionResultRepo;
	}
	
	/**
	 * Checks repeatedly for new submission and executed the code.
	 */
	@Scheduled(fixedDelay = 1)
	public void RunSubmission() {
		// Check the submission in the queue
		QueueMessageDTO messageDTO =  messageQueue.CheckMessageFromQueue();
		
		if(messageDTO == null) return;
		CodeExecuter codeExecuter = null;
		Submissions submission = null;
		SubmissionResult submissionResult = null;
		try {
			// Fetch the submission details
			submission = this.submissionsRepo.findById(Long.parseLong(messageDTO.Message)).get();
			
			// Fetch the problem details
			Problems problem = this.problemsRepo.findById(submission.getProblemId()).get();
			
			//Fetch the driver code for the language 
			DriverCode driverCode = this.driverCodeRepo.findByProblemIdAndLanguage(problem.getId(), submission.getLang()).get(0);
			
			// Get the code executor from code executor factory
			codeExecuter = CodeExecutorFactory.getCodeExecutor(submission.getLang());
			
			String workspace = StorageService.CreateNewLocalWorkSpace(String.valueOf(submission.getId()));
						
			codeExecuter.setWorkspace(workspace);
			
			// Initialize the code executor
			codeExecuter.SetExecutionFiles(problem.getHiddenTestCaseFile(), problem.getExpOutputFile(), driverCode.getDriverCodeFile(), submission.getCodeFile());
			
			// Compile and run the code
			ExecutionResult executionResult = codeExecuter.CompileAndRun();
			
			submissionResult = new SubmissionResult();
			submissionResult.setSubmissionId(submission.getId());
			submissionResult.setMemory(executionResult.Memory);
			submissionResult.setRuntime(executionResult.Runtime);
			submissionResult.setStatus(executionResult.Status);
			submissionResult.setTestCasePassed(executionResult.TestCasePassed);
			
			if(executionResult.ResultFile != null) {
				String resultFile = StorageService.SaveResult(executionResult.ResultFile);
				submissionResult.setResultFile(resultFile);
			}
			
			this.submissionResultRepo.save(submissionResult);
			
			submission.setStatus(SubmissionStatus.Completed);
			
			this.submissionsRepo.save(submission);
						
		}
		catch(Exception exception) {
			if(submission != null) {
				submissionResult = new SubmissionResult();
				submissionResult.setSubmissionId(submission.getId());
				submissionResult.setStatus(StatusMessage.InternalError);
				
				this.submissionResultRepo.save(submissionResult);
				
				submission.setStatus(SubmissionStatus.Completed);
				
				this.submissionsRepo.save(submission);
			}
			
		}finally {
			if(codeExecuter != null) codeExecuter.ClearWorkSpace();
			messageQueue.DeleteMessageFromQueue(messageDTO);
		}
		
		
	}
}
