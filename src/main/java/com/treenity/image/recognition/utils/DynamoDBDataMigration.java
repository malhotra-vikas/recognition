package com.treenity.image.recognition.utils;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

public class DynamoDBDataMigration {

    public static void main(String[] args) {
        String sourceTableName = "recognizedText.1";
        String destinationTableName = "recognizedTextEvent";
        
        // Initialize DynamoDB clients
        DynamoDbClient sourceDynamoDB = DynamoDbClient.builder()
                .region(Region.US_EAST_2)
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .credentialsProvider(ProfileCredentialsProvider.builder().profileName("VideosByRaf").build())
                .build();
        

        DynamoDbClient destinationDynamoDB = DynamoDbClient.builder()
                .region(Region.US_EAST_2)
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .credentialsProvider(ProfileCredentialsProvider.builder().profileName("VideosByRaf").build())
                .build();
        
        // Initialize a token for pagination
        Map<String, AttributeValue> lastEvaluatedKey = null;
        int countRead = 0;
        int countWritten = 0;

        do {
            // Create a scan request with pagination token
            ScanRequest.Builder scanRequestBuilder = ScanRequest.builder()
                    .tableName(sourceTableName)
                    .limit(1000) // Adjust the batch size as needed
                    .exclusiveStartKey(lastEvaluatedKey);
            
            ScanResponse scanResponse;
			try {
				scanResponse = sourceDynamoDB.scan(scanRequestBuilder.build());
				countRead = countRead + scanResponse.items().size();
				
				// Process and copy items to the destination table with an additional attribute
	            for (Map<String, AttributeValue> item : scanResponse.items()) {
	                Map<String, AttributeValue> modifiedItem = new HashMap<>(item);
	                modifiedItem.put("eventName", AttributeValue.builder().s("lomas de pachacamac 2023").build());
	                
	                PutItemRequest putItemRequest = PutItemRequest.builder()
	                        .tableName(destinationTableName)
	                        .item(modifiedItem)
	                        .build();
	                destinationDynamoDB.putItem(putItemRequest);
	                countWritten = countWritten + 1;
	            }

	            // Update the pagination token
	            lastEvaluatedKey = scanResponse.lastEvaluatedKey();
	            
			} catch (ProvisionedThroughputExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ResourceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RequestLimitExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InternalServerErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DynamoDbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AwsServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SdkClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            
        } while (lastEvaluatedKey != null);
        
        System.out.print(countRead);
        System.out.print(countWritten);


        // Close the clients
        sourceDynamoDB.close();
        destinationDynamoDB.close();
    }
}
