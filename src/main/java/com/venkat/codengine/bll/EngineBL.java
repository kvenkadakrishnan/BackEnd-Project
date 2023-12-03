package com.venkat.codengine.bll;

import org.springframework.stereotype.Service;

import com.venkat.codengine.Utils.FileStorage;
import com.venkat.codengine.Utils.ProgrammingLanguage;
import com.venkat.codengine.constants.SubmissionStatus;
import com.venkat.codengine.dto.Submission;
import com.venkat.codengine.dto.SubmissionResponse;
import com.venkat.codengine.entity.Submissions;
import com.venkat.codengine.repository.ProblemsRepo;
import com.venkat.codengine.repository.SubmissionsRepo;

@Service
public class EngineBL {
	private SubmissionsRepo submisionsRepo;
	private ProblemsRepo problemsRepo;

	EngineBL(SubmissionsRepo submisionsRepo, ProblemsRepo problemsRepo) {
		this.submisionsRepo = submisionsRepo;
		this.problemsRepo = problemsRepo;
	}

	public SubmissionResponse SubmitCode(Submission userSubmission) throws Exception {

		SubmissionResponse submissionResponse = new SubmissionResponse();

		// Check the users code is under the limit
		if (userSubmission.code.length() > 10240) {
			throw new Exception("File size is too large");
		}

		// Check the programming language
		if (! ProgrammingLanguage.supportedLanguauges.contains(userSubmission.languageExtension.toLowerCase())) {
			throw new Exception("Unsupported Language");
		}

		// Check the problem id is valid
		if (!problemsRepo.existsById(userSubmission.problemId)) {
			throw new Exception( "Problem not found");
		}

		String codeFilePath;
		try {
			codeFilePath = FileStorage.SaveSubmittedCode(userSubmission.code);
		} catch (Exception e) {
			throw new Exception( "Check the code and try again");
		}

		Submissions submissions = new Submissions();
		submissions.setLang(userSubmission.languageExtension);
		submissions.setCodeFile(codeFilePath);
		submissions.setProblemId(userSubmission.problemId);
		submissions.setStatus(SubmissionStatus.Waiting);

		try {
			submissions = submisionsRepo.save(submissions);
			submissionResponse.SubmmissionId = submissions.getId();
			submissionResponse.Message = SubmissionStatus.Received;
		}catch (Exception e){
			FileStorage.DeleteFile(codeFilePath);
			throw new Exception("Unexpected error occured, please try again");
		}
		
		// TODO add to queue

		return submissionResponse;
	}
}
