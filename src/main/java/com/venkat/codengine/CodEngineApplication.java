package com.venkat.codengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CodEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodEngineApplication.class, args);
	}

	@SqsListener(value = "CompilerQueue",deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
	public void LoadMessageFromQueue(String submissions) {
		System.out.println(submissions);
	}
}
