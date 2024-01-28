package com.venkat.codeexecutor.worker.submissionprocessor;

import java.io.FileInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.venkat.codeexecutor.messagequeue.MessageQueue;
import com.venkat.codeexecutor.webserver.constants.StatusMessage;
import com.venkat.codeexecutor.webserver.constants.SubmissionStatus;
import com.venkat.codeexecutor.webserver.dto.QueueMessageDTO;
import com.venkat.codeexecutor.webserver.entity.DriverCode;
import com.venkat.codeexecutor.webserver.entity.Problems;
import com.venkat.codeexecutor.webserver.entity.SubmissionResult;
import com.venkat.codeexecutor.webserver.entity.Submissions;
import com.venkat.codeexecutor.webserver.repository.DriverCodeRepo;
import com.venkat.codeexecutor.webserver.repository.ProblemsRepo;
import com.venkat.codeexecutor.webserver.repository.SubmissionResultRepo;
import com.venkat.codeexecutor.webserver.repository.SubmissionsRepo;
import com.venkat.codeexecutor.webserver.storageservice.IFileStorageService;
import com.venkat.codeexecutor.worker.codeexecutor.CodeExecuter;
import com.venkat.codeexecutor.worker.codeexecutor.CodeExecutorFactory;
import com.venkat.codeexecutor.worker.dto.ExecutionResult;
import com.venkat.codeexecutor.worker.utils.FileUtilities;

@Component
public class SubmissionProcessor {
	
	@Value("${files.result}")
	private String resultFolderName; 
	
	private MessageQueue messageQueue;
	private SubmissionsRepo submissionsRepo;
	private ProblemsRepo problemsRepo;
	private DriverCodeRepo driverCodeRepo;
	private SubmissionResultRepo submissionResultRepo;
	private IFileStorageService fileStorageService;
	private CodeExecutorFactory codeExecutorFactory;
	
	public SubmissionProcessor(MessageQueue messageQueue,SubmissionsRepo submissionsRepo,ProblemsRepo problemsRepo,CodeExecutorFactory codeExecutorFactory, 
			DriverCodeRepo driverCodeRepo, SubmissionResultRepo submissionResultRepo,IFileStorageService fileStorageService) {
		this.messageQueue = messageQueue;
		this.problemsRepo = problemsRepo;
		this.submissionsRepo = submissionsRepo;
		this.driverCodeRepo = driverCodeRepo;
		this.submissionResultRepo = submissionResultRepo;
		this.fileStorageService = fileStorageService;
		this.codeExecutorFactory = codeExecutorFactory;
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
			codeExecuter = this.codeExecutorFactory.getCodeExecutor(submission.getLang());
			
			String workspace = FileUtilities.CreateNewLocalWorkSpace(String.valueOf(submission.getId()));
						
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
				byte[] fileContent = new byte[(int)executionResult.ResultFile.length()];
				FileInputStream fis = new FileInputStream(executionResult.ResultFile);
				fis.read(fileContent);
				fis.close();
				String resultFile = this.fileStorageService.SaveText(fileContent,resultFolderName);
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
