package com.treenity.image.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;


public class GenerateImageMetadataLambda {
	PictureMetadataBuilder metadataBuilder = new PictureMetadataBuilder();
	LambdaLogger logger = null;
	
	public Void handleRequest(SQSEvent event, Context context) {
	    logger = context.getLogger();


     // Iterate through all messages in the Queue, process and delete
        if (!event.getRecords().isEmpty()) {
        	for (SQSMessage msg : event.getRecords()) {
                // Process each message
                
        		String messageBody = msg.getBody();
        		logger.log("Gotcha Received message: " + messageBody);

	            // Delete the message to avoid re-processing
	            deleteMessageFromQueue(msg);
	            
            	metadataBuilder.generateMetadata(messageBody);
            	
            	insertToProcessMetadataQueue(messageBody);
        	}
        } else {
            logger.log("No messages received.");
        }

       
        return null;
    }
	
	private void insertToProcessMetadataQueue(String message) {
        SqsClient sqsClient = SqsClient.builder().build();

		String queueUrl = "https://sqs.us-east-2.amazonaws.com/018701121298/MetadataUpdated1"; 

        try {
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(message)
                    .build();
            sqsClient.sendMessage(sendMsgRequest);
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
		

		
	}

	private void deleteMessageFromQueue(SQSEvent.SQSMessage msg) {
        SqsClient sqsClient = SqsClient.builder().build();

        String queueUrl = "https://sqs.us-east-2.amazonaws.com/018701121298/ImagesUploaded1"; 
        String receiptHandle = msg.getReceiptHandle();

        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();

        sqsClient.deleteMessage(deleteRequest);
        logger.log("Deleted message: " + msg.getBody());

    }

}
