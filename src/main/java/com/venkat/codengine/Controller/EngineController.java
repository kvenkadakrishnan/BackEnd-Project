package com.venkat.codengine.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.venkat.codengine.bll.EngineBL;
import com.venkat.codengine.models.Submission;
import com.venkat.codengine.models.SubmissionResult;

@RestController
public class EngineController {
	private EngineBL engineBL;
	public EngineController(EngineBL engineBL) {
		this.engineBL = engineBL;
	}
	
	@PostMapping
	public ResponseEntity<Object> RunCode(@RequestBody Submission submission){
		try {
			SubmissionResult submissionResult = this.engineBL.RunCode(submission);
			return ResponseEntity.status(HttpStatus.OK).body(submissionResult);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	
}
