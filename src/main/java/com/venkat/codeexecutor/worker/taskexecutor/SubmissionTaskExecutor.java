package com.venkat.codeexecutor.worker.taskexecutor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.venkat.codeexecutor.messagequeue.MessageQueue;
import com.venkat.codeexecutor.webserver.dto.QueueMessageDTO;
import com.venkat.codeexecutor.worker.submissionprocessor.SubmissionProcessor;

@Component
public class SubmissionTaskExecutor {
	private MessageQueue messageQueue;
	private SubmissionProcessor submissionProcessor;
	public SubmissionTaskExecutor(MessageQueue messageQueue, SubmissionProcessor submissionProcessor) {
		this.messageQueue = messageQueue;
		this.submissionProcessor = submissionProcessor;
	}
	
	@Scheduled(fixedDelay = 1)
	public void Execute() {
		QueueMessageDTO messageDTO = null;
		messageDTO =  messageQueue.CheckMessageFromQueue();
		if(messageDTO == null) return;
		try {
			this.submissionProcessor.RunSubmission(Long.parseLong(messageDTO.Message));
		}catch (Exception e) {
			// No implementation required
		}finally {
			messageQueue.DeleteMessageFromQueue(messageDTO);
		}
	}
}
