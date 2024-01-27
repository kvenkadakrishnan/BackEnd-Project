package com.venkat.codeexecutor.webserver.service;

import org.springframework.stereotype.Service;

import com.venkat.codeexecutor.messagequeue.MessageQueue;
import com.venkat.codeexecutor.webserver.constants.ProgrammingLanguage;
import com.venkat.codeexecutor.webserver.constants.SubmissionStatus;
import com.venkat.codeexecutor.webserver.dto.Submission;
import com.venkat.codeexecutor.webserver.dto.SubmissionResponse;
import com.venkat.codeexecutor.webserver.entity.Submissions;
import com.venkat.codeexecutor.webserver.repository.ProblemsRepo;
import com.venkat.codeexecutor.webserver.repository.SubmissionsRepo;
import com.venkat.codeexecutor.webserver.storageservice.IFileStorageService;

@Service
public class SubmissionBL {
	private SubmissionsRepo submisionsRepo;
	private ProblemsRepo problemsRepo;
	private MessageQueue queueService;
	private IFileStorageService storageService;

	SubmissionBL(SubmissionsRepo submisionsRepo, ProblemsRepo problemsRepo, MessageQueue queueService,IFileStorageService storageService) {
		this.submisionsRepo = submisionsRepo;
		this.problemsRepo = problemsRepo;
		this.queueService = queueService;
		this.storageService = storageService;
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

		String codeFilePath;
		try {
			codeFilePath = this.storageService.SaveText(userSubmission.code,"");
		} catch (Exception e) {
			throw new Exception( "Check the code and try again");
		}

		Submissions submissions = new Submissions();
		submissions.setLang(userSubmission.languageExtension);
		submissions.setCodeFile(codeFilePath);
		submissions.setProblemId(userSubmission.problemId);
		submissions.setStatus(SubmissionStatus.Waiting);

		try {
			submissions = this.submisionsRepo.save(submissions);
			submissionResponse.SubmmissionId = submissions.getId();
			submissionResponse.Message = SubmissionStatus.Received;
			this.queueService.SendMessage(String.valueOf(submissions.getId()));
		}catch (Exception e){
			this.storageService.DeleteFile("",codeFilePath);
			throw new Exception("Unexpected error occured, please try again");
		}
		
		return submissionResponse;
	}
}
