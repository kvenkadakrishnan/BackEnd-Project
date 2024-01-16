package com.venkat.codengine.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

@Configuration
public class AWSConfig {
	
	@Value("${cloud.aws.region.static}")
	private String region;
	
	@Value("${cloud.aws.credentials.access-key}")
	private String awsAccessKey;
	
	@Value("${cloud.aws.credentials.secret-key}")
	private String awsSecretKey;
	
	@Bean
	public QueueMessagingTemplate queueMessagingTemplate() {
		return new QueueMessagingTemplate(amazonSQSAsync());
	}

	@Bean
	@Primary
	public AmazonSQSAsync amazonSQSAsync() {
		return AmazonSQSAsyncClientBuilder.standard().withRegion(Regions.EU_NORTH_1)
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey))).build();
	}
	
	@Bean
	public AmazonSQS amazonSQS() {
		return AmazonSQSClientBuilder.standard().withRegion(Regions.EU_NORTH_1)
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsAccessKey, awsSecretKey))).build();
	}
	
}
