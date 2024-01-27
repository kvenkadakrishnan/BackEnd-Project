package com.venkat.codeexecutor.messagequeue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.venkat.codeexecutor.webserver.dto.QueueMessageDTO;

@Component
public class MessageQueue {

	private AmazonSQS sqs;
	
	private QueueMessagingTemplate queueMessagingTemplate;
	
	@Value("${messagequeue.name}")
	private String queueUrl;
	
	@Value("${cloud.aws.end-point.uri}")
	private String endPoint;
	
	public MessageQueue(AmazonSQS amazonSQS, QueueMessagingTemplate queueMessagingTemplate) {
		this.sqs = amazonSQS;
		this.queueMessagingTemplate = queueMessagingTemplate;
	}

	public QueueMessageDTO CheckMessageFromQueue() {
		try {
			ReceiveMessageResult sqsMessage = this.sqs.receiveMessage(queueUrl);

	    	if(sqsMessage.getMessages().size() > 0) {
	    		Message message = sqsMessage.getMessages().get(0);
	    		QueueMessageDTO queueMessage = new QueueMessageDTO();
	    		queueMessage.Message = message.getBody();
	    		queueMessage.Receipt = message.getReceiptHandle();
	    		return queueMessage;
	    	}else {
	    		return null;
	    	}
		}catch (Exception e) {
			System.out.println("Error in recieving message");
			return null;
		}
		
	}
	
	public void SendMessage(String msg) throws Exception{
		try {
			MessageBuilder<String> message  = MessageBuilder.withPayload(msg); 
			queueMessagingTemplate.send(endPoint, message.build());
		}catch(Exception ex){
			throw ex;
		}
	}
	
	public void DeleteMessageFromQueue(QueueMessageDTO queueMessgae) {
		this.sqs.deleteMessage(queueUrl, queueMessgae.Receipt);
	}

}
