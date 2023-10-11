package com.awstest.sqs.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sqs")
public class SQSController {

	Logger logger = LoggerFactory.getLogger(SQSController.class);
	
	@Autowired
	private QueueMessagingTemplate queueMessagingTemplate;
	
	@Autowired
	private SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory;
	
	@Value("${cloud.aws.end-point.uri}")
	private String endpoint;
	
	@Value("${cloud.aws.region.static}")
	private String region;
	
	@Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
	
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;
    
	
	@RequestMapping(value = "/getMessage", method = RequestMethod.GET)
	public void getMessage() {
		System.out.println(queueMessagingTemplate.receive(endpoint));
//		SimpleMessageListenerContainer createSimpleMessageListenerContainer = simpleMessageListenerContainerFactory.createSimpleMessageListenerContainer();
//		createSimpleMessageListenerContainer.start();
	}
	
	@RequestMapping(value = "/send/{message}", method = RequestMethod.POST)
	public void sendMessageToQueue(@PathVariable("message") String message){
//		System.out.println("send: "+endpoint+" : "+message);
		queueMessagingTemplate.send(endpoint,MessageBuilder.withPayload(message).build());
		
	}
	
	
	
	
	
	@SqsListener("test")
	public void getMessageFromSQS(String message) {
		
		System.out.println(message);
		logger.info("Message Received from AWS SQS Queue- "+message);
	}
}
