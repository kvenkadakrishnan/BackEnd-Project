package com.venkat.codeexecutor.webserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.venkat.codeexecutor.webserver.dto.Submission;
import com.venkat.codeexecutor.webserver.dto.SubmissionResponse;
import com.venkat.codeexecutor.webserver.service.SubmissionBL;

@RestController
public class SubmissionsController {
	private SubmissionBL submissionBl;
	public SubmissionsController(SubmissionBL submissionBl) {
		this.submissionBl = submissionBl;
	}
	
	@PostMapping("submitCode")
	public ResponseEntity<Object> SubmitCode(@RequestBody Submission submission){
		try {
			SubmissionResponse submissionResult = this.submissionBl.SubmitCode(submission);
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(submissionResult);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
		}
	}
	
	@GetMapping("result")
	public ResponseEntity<Object> CheckResult(Long submissionId){
		String result = this.submissionBl.CheckResult(submissionId);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
	
	@GetMapping("problem")
	public ResponseEntity<Object> GetProblem(int problemId){
		String result = this.submissionBl.GetProblem(problemId);
		return ResponseEntity.status(HttpStatus.OK).body(result);
	}
}
