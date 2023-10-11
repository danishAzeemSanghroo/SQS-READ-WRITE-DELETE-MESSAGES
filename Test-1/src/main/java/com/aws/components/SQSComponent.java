package com.aws.components;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import jakarta.annotation.PostConstruct;

@Component("sQSComponent") 
public class SQSComponent {
	
	@Value("${cloud.aws.end-point.uri}")
	private String endpoint;
	
	@Value("${cloud.aws.region.static}")
	private String region;
	
	@Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
	
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;
    
    private AmazonSQS amazonSQS;

	
	private static final Logger logger = LoggerFactory.getLogger(SQSComponent.class);
	
    @PostConstruct
    private void postConstructor() {

        logger.info("SQS URL: " + endpoint);

        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(accessKey, secretKey)
        );

        this.amazonSQS = AmazonSQSClientBuilder.standard().withCredentials(awsCredentialsProvider).build();
    }

    public void startListeningToMessages() {

        final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(endpoint)
                .withMaxNumberOfMessages(1)
                .withWaitTimeSeconds(3);

        while (true) {

            final List<com.amazonaws.services.sqs.model.Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).getMessages();

            for (com.amazonaws.services.sqs.model.Message messageObject : messages) {
                String message = messageObject.getBody();

                logger.info("Received message: " + message);

                deleteMessage(messageObject);
            }
        }
    }

    private void deleteMessage(com.amazonaws.services.sqs.model.Message messageObject) {

        final String messageReceiptHandle = messageObject.getReceiptHandle();
        amazonSQS.deleteMessage(new DeleteMessageRequest(endpoint, messageReceiptHandle));

    }

}
