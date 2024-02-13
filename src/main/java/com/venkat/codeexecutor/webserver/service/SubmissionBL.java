package com.venkat.codeexecutor.webserver.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.venkat.codeexecutor.messagequeue.MessageQueue;
import com.venkat.codeexecutor.webserver.constants.ProgrammingLanguage;
import com.venkat.codeexecutor.webserver.constants.SubmissionStatus;
import com.venkat.codeexecutor.webserver.dto.Submission;
import com.venkat.codeexecutor.webserver.dto.SubmissionResponse;
import com.venkat.codeexecutor.webserver.entity.SubmissionResult;
import com.venkat.codeexecutor.webserver.entity.Submissions;
import com.venkat.codeexecutor.webserver.repository.ProblemsRepo;
import com.venkat.codeexecutor.webserver.repository.SubmissionResultRepo;
import com.venkat.codeexecutor.webserver.repository.SubmissionsRepo;
import com.venkat.codeexecutor.webserver.storageservice.IFileStorageService;

@Service
public class SubmissionBL {
	
	@Value("${files.result}")
	private String result;
	
	private SubmissionsRepo submisionsRepo;
	private ProblemsRepo problemsRepo;
	private MessageQueue queueService;
	private IFileStorageService storageService;
	private SubmissionResultRepo submissionResultRepo;
	
	@Value("${files.submission}")
	private String submissionFolderName;

	SubmissionBL(SubmissionsRepo submisionsRepo, ProblemsRepo problemsRepo, MessageQueue queueService,
			 SubmissionResultRepo submissionResultRepo,IFileStorageService storageService) {
		this.submisionsRepo = submisionsRepo;
		this.problemsRepo = problemsRepo;
		this.queueService = queueService;
		this.storageService = storageService;
		this.submissionResultRepo= submissionResultRepo; 
	}

	public SubmissionResponse SubmitCode(Submission userSubmission) throws Exception {

		SubmissionResponse submissionResponse = new SubmissionResponse();

		// Check the users code is under the limit
		if (userSubmission.code.length() > 10240) {
			throw new Exception("File size is too large");
		}

		// Check the programming language
		if (! ProgrammingLanguage.SUPPORTED_LANGUAGES.contains(userSubmission.languageExtension.toLowerCase())) {
			throw new Exception("Unsupported Language");
		}

		// Check the problem id is valid
		if (!problemsRepo.existsById(userSubmission.problemId)) {
			throw new Exception( "Problem not found");
		}

		// Save the submitted code in storage service
		String codeFilePath;
		try {
			codeFilePath = this.storageService.SaveText(userSubmission.code,submissionFolderName);
		} catch (Exception e) {
			throw new Exception( "Unexpected error occured, please try again");
		}

		// Create submission entity
		Submissions submissions = new Submissions();
		submissions.setLang(userSubmission.languageExtension);
		submissions.setCodeFile(codeFilePath);
		submissions.setProblemId(userSubmission.problemId);
		submissions.setStatus(SubmissionStatus.Waiting);

		// Persist the submission entity in db and put the submission id into queue to be processed by the worker
		try {
			submissions = this.submisionsRepo.save(submissions);
			submissionResponse.SubmmissionId = submissions.getId();
			submissionResponse.Message = SubmissionStatus.Received;
			this.queueService.SendMessage(String.valueOf(submissions.getId()));
		}catch (Exception e){
			this.storageService.DeleteFile(submissionFolderName,codeFilePath);
			this.submisionsRepo.deleteById(submissions.getId());
			throw new Exception("Unexpected error occured, please try again");
		}
		
		return submissionResponse;
	}
	
	public String CheckResult(Long submissionId) {
		SubmissionResult submissionResult = null;
		try {
			submissionResult = this.submissionResultRepo.findById(submissionId).get();
			if(submissionResult == null) return "Pending execution..";
			return this.storageService.GetTextContent(this.result, submissionResult.getResultFile());
		}catch (Exception e) {
			System.out.println("Error in Submission bl: "+ e.getMessage());
			return "Pending execution..";
		}
	}
}
