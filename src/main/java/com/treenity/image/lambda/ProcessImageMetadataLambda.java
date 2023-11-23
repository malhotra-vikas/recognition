package com.treenity.image.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;


public class ProcessImageMetadataLambda {
	PictureMetadataProcessor metadataProcessor = new PictureMetadataProcessor();
	LambdaLogger logger = null;
	
	String eventName;
    String photographerId;
    String imageId;
    int firstIndex;
    int lastIndex;
	
	public Void handleRequest(SQSEvent event, Context context) {
	    logger = context.getLogger();

	 // Iterate through all messages in the Queue, process and delete
        if (!event.getRecords().isEmpty()) {
        	for (SQSMessage msg : event.getRecords()) {
                // Process each message
                
        		String messageBody = msg.getBody();
        		logger.log("Gotcha Received message: " + messageBody);
	            // MessadgeBosy has EventName:PhotographerName:ImageId

	            //Event1:Vikas:DSC_65675.jpg
        		
	            // Delete the message to avoid re-processing
	            deleteMessageFromQueue(msg);
	            
	          //Event1:Vikas:DSC_65675.jpg
	            firstIndex = messageBody.indexOf(":");
	            eventName = messageBody.substring(0, firstIndex);	    
        		logger.log("eventName  -:" + eventName);

        		lastIndex = messageBody.lastIndexOf(":");
	            photographerId = messageBody.substring(eventName.length()+1, lastIndex);
        		logger.log("photographerName  -:" + photographerId);
        		
        		imageId = messageBody.substring(lastIndex+1, messageBody.length());
        		logger.log("imageId  -:" + imageId);
        		
                metadataProcessor.processMetadata(eventName, photographerId, imageId);
        	}
        } else {
            logger.log("No messages received.");
        }
                
        return null;
    }
	
	
	
	private void deleteMessageFromQueue(SQSEvent.SQSMessage msg) {
        SqsClient sqsClient = SqsClient.builder().build();

        String queueUrl = "https://sqs.us-east-2.amazonaws.com/018701121298/MetadataUpdated1"; 
        String receiptHandle = msg.getReceiptHandle();

        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();

        sqsClient.deleteMessage(deleteRequest);
        logger.log("Deleted message: " + msg.getBody());

    }

}
