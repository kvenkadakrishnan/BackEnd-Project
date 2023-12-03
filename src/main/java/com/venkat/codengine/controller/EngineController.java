package com.venkat.codengine.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.venkat.codengine.bll.EngineBL;
import com.venkat.codengine.dto.Submission;
import com.venkat.codengine.dto.SubmissionResponse;

@RestController
public class EngineController {
	private EngineBL engineBL;
	public EngineController(EngineBL engineBL) {
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
