package com.treenity.image.recognition.utils;

import java.awt.ItemSelectable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;


import com.treenity.image.ddb.DDBCrud;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;


public class ImageKeyUtils {
	String imageId;
	String imageURL;
	String highConfidenceDetectedTextList;
	ArrayList<String> detectedTextList;
	
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	
	
	public String getHighConfidenceDetectedTextList() {
		return highConfidenceDetectedTextList;
	}
	public void setHighConfidenceDetectedTextList(String highConfidenceDetectedTextList) {
		this.highConfidenceDetectedTextList = highConfidenceDetectedTextList;
	}
	

	public ArrayList<String> getDetectedTextList() {
		return detectedTextList;
	}
	public void setDetectedTextList(ArrayList<String> detectedTextList) {
		this.detectedTextList = detectedTextList;
	}
	
	public void transformAndPersistToDDB(ImageKeyUtils imageKeyUtils, String eventName) {
		
		HashMap<String, String> imageKeyUrlMap = new HashMap<String, String>();
		HashMap<String, HashMap<String, String>> persistenceReadyMap = new HashMap<>();
		DDBHandler ddbHandler = new DDBHandler();
		DDBCrud crud = new DDBCrud();
		
		String imageUrl = imageKeyUtils.getImageURL();
		ArrayList<String> detectedTextList = imageKeyUtils.getDetectedTextList();
		String detectedTextToProcess;
        DynamoDbClient dynamodbClient = DependencyFactory.ddbClient();   
		
		Map<String, AttributeValue> fetchedEntry = null;
		Map<String, AttributeValue> fetchedEventEntry = null;
		
		int size = detectedTextList.size();
        //System.out.println(size);

		for (int i=0; i<size; i++) {
			fetchedEntry = null;
			fetchedEventEntry = null;
			
			detectedTextToProcess = detectedTextList.get(i);
			//System.out.println("detectedTextToProcess : " + detectedTextToProcess);

			
			// Fetch an existing Entry for the detectedText and Event
			QueryResponse queryResponse = ddbHandler.fetchDDBEntry(detectedTextToProcess, eventName);
			if (queryResponse != null) {
				
				try {
					fetchedEntry = queryResponse.items().get(0);
					System.out.println("Fetched existing entry for this compination :");

				} catch (java.lang.IndexOutOfBoundsException e) {
					// TODO Auto-generated catch block
					System.out.println("No entry for this compination :");
					fetchedEntry = null;
				}
			}
			// Process the query results
			/*
			 * for (Map<String, AttributeValue> item : queryResponse.items()) { // Access
			 * the selected attributes String attribute1Value = item.get("Attribute1").s();
			 * String attribute2Value = item.get("Attribute2").s();
			 * 
			 * System.out.println("Attribute1: " + attribute1Value);
			 * System.out.println("Attribute2: " + attribute2Value); }
			 */
			//System.out.println(fetchedEntry.size());

            String ddbTableName = DependencyFactory.getDetectedTextddbTableName();
            
            String pkKeyName = "detectedText";
            String eventNameKey = "eventName";
            String listAttributeKeyName = "imageArtifacts";
            
			// If existing entry does not exist 
			if (fetchedEntry == null || fetchedEntry.size() == 0)  {
				//Exiting entry for this detected text does not exists./ Create a new one
				System.out.println("No Existing entry, lets create new");

		        HashMap<String, String> attributesHashMap = new HashMap<>();
		        attributesHashMap.put(pkKeyName, detectedTextToProcess);
		        attributesHashMap.put(eventNameKey, eventName);
		        attributesHashMap.put("imageArtifacts", imageUrl);
		        ddbHandler.putItemInTable(dynamodbClient, ddbTableName, attributesHashMap);
			} else {
				// 
				System.out.println("Existing entry, lets add to it");
				String existingImageUrls = null;
				/*
				 * Map<String, AttributeValue> existingItem;
				 * 
				 * // Construct the key with which to query HashMap<String, AttributeValue>
				 * keyToGet = new HashMap<>();
				 * 
				 * keyToGet.put(pkKeyName, AttributeValue.builder() .s(detectedTextToProcess) //
				 * .s for string type attributes, you can use .n for number types, etc.
				 * .build()); keyToGet.put(eventNameKey, AttributeValue.builder() .s(eventName)
				 * // .s for string type attributes, you can use .n for number types, etc.
				 * .build()); existingItem = crud.readItem(ddbTableName, keyToGet,
				 * listAttributeKeyName);
				 */
		        
		    	existingImageUrls = fetchedEntry.get(listAttributeKeyName).s();
				System.out.println("existingImageUrls - " + existingImageUrls);

				
		        HashMap<String, String> attributesHashMap = new HashMap<>();
		        attributesHashMap.put(pkKeyName, detectedTextToProcess);
		        attributesHashMap.put(eventNameKey, eventName);
		        attributesHashMap.put("imageArtifacts", existingImageUrls +", " + imageUrl);
		        ddbHandler.putItemInTable(dynamodbClient, ddbTableName, attributesHashMap);

			}
			
			// Send message to Download Queue
			insertToDownloadQueue(detectedTextToProcess);
		}
		
        
		
	}
	
	
	private void insertToDownloadQueue(String message) {
        SqsClient sqsClient = SqsClient.builder().build();

		String queueUrl = "https://sqs.us-east-2.amazonaws.com/018701121298/ImagesReadyToDownload1"; 

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

}
