package com.venkat.codeexecutor.webserver.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.venkat.codeexecutor.webserver.dto.Submission;
import com.venkat.codeexecutor.webserver.dto.SubmissionResponse;
import com.venkat.codeexecutor.webserver.service.SubmissionBL;

@RestController
public class SubmissionsController {
	private SubmissionBL engineBL;
	public SubmissionsController(SubmissionBL engineBL) {
		this.engineBL = engineBL;
	}
	
	@PostMapping
	public ResponseEntity<Object> SubmitCode(@RequestBody Submission submission){
		try {
			SubmissionResponse submissionResult = this.engineBL.SubmitCode(submission);
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(submissionResult);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
		}
	}
	
	
}
