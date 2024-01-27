package com.venkat.codeexecutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class CodeExecutorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeExecutorApplication.class, args);
	}

}
