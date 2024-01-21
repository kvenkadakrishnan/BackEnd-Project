package com.venkat.codengine.worker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.venkat.codengine.dto.QueueMessageDTO;

@Component
public class MessageQueue {

	private AmazonSQS sqs;
	
	@Value("${messagequeue.name}")
	private String Queue;
	
	public MessageQueue(AmazonSQS amazonSQS) {
		this.sqs = amazonSQS;
	}

	public QueueMessageDTO CheckMessageFromQueue() {
		try {
			ReceiveMessageResult sqsMessage = this.sqs.receiveMessage(Queue);

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
	
	public void DeleteMessageFromQueue(QueueMessageDTO queueMessgae) {
		this.sqs.deleteMessage(Queue, queueMessgae.Receipt);
	}

}
