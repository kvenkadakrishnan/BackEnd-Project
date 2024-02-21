package com.venkat.codeexecutor.worker.taskexecutor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.venkat.codeexecutor.worker.submissionprocessor.SubmissionProcessor;

@RestController
public class SubmissionTaskExecutor {
	private SubmissionProcessor submissionProcessor;
	public SubmissionTaskExecutor(SubmissionProcessor submissionProcessor) {
		this.submissionProcessor = submissionProcessor;
	}
		
	/**
	 * Executes the task assigned by the SQS Daemon process.
	 * @param msg SQS message body contains submission id.
	 * @return Whether the task is executed successfully or not.
	 */
	@PostMapping("/")
	public ResponseEntity<Object> Execute(@RequestBody  String msg) {
		try {
			System.out.println("Started processing... message: "+msg);
			this.submissionProcessor.RunSubmission(Long.parseLong(msg));
			return ResponseEntity.status(HttpStatus.OK).body("Successfully processed the message");
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to process the message");
		}

	}
}
