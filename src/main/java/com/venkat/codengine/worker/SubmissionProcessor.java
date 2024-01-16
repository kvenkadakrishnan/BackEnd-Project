package com.venkat.codengine.worker;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.venkat.codengine.Utils.StorageService;
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
		
		try {
			// Fetch the submission details
			Submissions submission = this.submissionsRepo.getReferenceById(Long.getLong(messageDTO.Message));
			
			// Fetch the problem details
			Problems problem = this.problemsRepo.getReferenceById(submission.getProblemId());
			
			//Fetch the driver code for the language 
			DriverCode driverCode = this.driverCodeRepo.findByProblemIdAndLanguage(problem.getId(), submission.getLang()).get(0);
			
			// Get the code executor from code executor factory
			CodeExecuter codeExecuter = CodeExecutorFactory.getCodeExecutor(submission.getLang());
			
			String workspace = StorageService.CreateNewLocalWorkSpace(String.valueOf(submission.getId()));
			
			codeExecuter.setWorkspace(workspace);
			
			// Initialize the code executor
			boolean initializeExecuter = codeExecuter.SetExecutionFiles(problem.getHiddenTestCaseFile(), problem.getExpOutputFile(), driverCode.getDriverCodeFile(), submission.getCodeFile());

			if(!initializeExecuter) throw new Exception("Initializing code executor exception");
			
			// Compile and run the code
			ExecutionResult executionResult = codeExecuter.CompileAndRun();
			
			String resultFile = StorageService.SaveResult(executionResult.ResultFile);
			
			SubmissionResult submissionResult = new SubmissionResult();
			submissionResult.setSubmissionId(submission.getId());
			submissionResult.setMemory(executionResult.Memory);
			submissionResult.setRuntime(executionResult.Runtime);
			submissionResult.setStatus(executionResult.Status);
			submissionResult.setTestCasePassed(executionResult.TestCasePassed);
			submissionResult.setResultFile(resultFile);
			
			this.submissionResultRepo.save(submissionResult);
			
			submission.setStatus(SubmissionStatus.Completed);
			
			this.submissionsRepo.save(submission);
			
			codeExecuter.ClearWorkSpace();
			
			messageQueue.DeleteMessageFromQueue(messageDTO);
		}
		catch(Exception exception) {
			// No implementation required
		}
		
		
	}
}
