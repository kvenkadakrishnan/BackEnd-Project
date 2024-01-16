package com.venkat.codengine.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class QueueService {
	
	@Value("${cloud.aws.end-point.uri}")
	private String endPoint;

	private QueueMessagingTemplate queueMessagingTemplate;

	public QueueService(QueueMessagingTemplate queueMessagingTemplate) {
		this.queueMessagingTemplate = queueMessagingTemplate;
	}
	public boolean sendMessage(String msg) {
		try {
			MessageBuilder<String> message  = MessageBuilder.withPayload(msg); 
			queueMessagingTemplate.send(endPoint, message.build());
			return true;
		}catch(Exception ex){
			return false;
		}
		
	}
}
