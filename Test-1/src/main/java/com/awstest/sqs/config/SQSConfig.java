package com.awstest.sqs.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

@Configuration
public class SQSConfig {

	
	@Value("${cloud.aws.end-point.uri}")
	private String endpoint;
	@Value("${cloud.aws.region.static}")
	private String region;
	@Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;
    
    
    @Bean
    public QueueMessagingTemplate queuMessagingTemplate() {
    	System.out.println("Connection Successful");
    	return new QueueMessagingTemplate(amazonSQSAsync());
    }
    
    @Primary
    @Bean
    public AmazonSQSAsync amazonSQSAsync() {
    	System.out.println("QueueMessagingTemplate: "+accessKey+":"+secretKey+":"+region);
    	return AmazonSQSAsyncClientBuilder.standard().withRegion(Regions.US_EAST_1)
    			.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey,secretKey)))
    			.build();
    }
    
    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory() {
        SimpleMessageListenerContainerFactory msgListenerContainerFactory = new SimpleMessageListenerContainerFactory();
        msgListenerContainerFactory.setAmazonSqs(amazonSQSAsync());
        return msgListenerContainerFactory;
    }
    
	
    @Bean
    public QueueMessageHandler queueMessageHandler() {
        QueueMessageHandlerFactory queueMsgHandlerFactory = new QueueMessageHandlerFactory();
        queueMsgHandlerFactory.setAmazonSqs(amazonSQSAsync());
        QueueMessageHandler queueMessageHandler = queueMsgHandlerFactory.createQueueMessageHandler();
        List<HandlerMethodArgumentResolver> list = new ArrayList<>();
        HandlerMethodArgumentResolver resolver = new PayloadMethodArgumentResolver(new MappingJackson2MessageConverter());
        list.add(resolver);
        queueMessageHandler.setArgumentResolvers(list);
        return queueMessageHandler;
    }

   	
}
