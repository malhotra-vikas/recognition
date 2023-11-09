package com.treenity.image.recognition.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;


import com.treenity.image.ddb.DDBCrud;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;


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
	
	public void transformAndPersistToDDB(ImageKeyUtils imageKeyUtils) {
		
		HashMap<String, String> imageKeyUrlMap = new HashMap<String, String>();
		HashMap<String, HashMap<String, String>> persistenceReadyMap = new HashMap<>();
		DDBHandler ddbHandler = new DDBHandler();
		
		String imageId = imageKeyUtils.getImageId();
		String imageUrl = imageKeyUtils.getImageURL();
		ArrayList<String> detectedTextList = imageKeyUtils.getDetectedTextList();
		String detectedTextToProcess;
        DynamoDbClient dynamodbClient = DependencyFactory.ddbClient();   
		
		Map<String, AttributeValue> fetchedEntry = null;
		
		int size = detectedTextList.size();
        //System.out.println(size);

		for (int i=0; i<size; i++) {
			fetchedEntry = null;
			
			detectedTextToProcess = detectedTextList.get(i);
			System.out.println("detectedTextToProcess : " + detectedTextToProcess);

			
			// Fetch an existing Entry for the detectedText
			fetchedEntry = ddbHandler.fetchDDBEntry(detectedTextToProcess);
			
			System.out.println("Number of Existing entries for selected detectedTextToProcess :"+ fetchedEntry.size());
			//System.out.println(fetchedEntry.size());

            String ddbTableName = "recognizedText.1";
            String pkKeyName = "detectedText";
            String listAttributeKeyName = "imageArtifacts";
            
			// If existing entry does not exist 
			if (fetchedEntry == null || fetchedEntry.size() == 0)  {
				// Exiting entry for this detected text does not exists./ Create a new one

	            
	            Map<String, AttributeValue> itemKey = new HashMap<>();
	            itemKey.put(pkKeyName, AttributeValue.builder().s(detectedTextToProcess).build());

	            AttributeValue listAttributeValue = AttributeValue.builder()
	                    .l(Arrays.asList(AttributeValue.builder().s(imageId).build(),
	                                     AttributeValue.builder().s(imageUrl).build()))
	                    .build();
	            
	            Map<String, AttributeValue> attributeValues = new HashMap<>();
	            attributeValues.put(":val", listAttributeValue);

	            UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
	                    .tableName(ddbTableName)
	                    .key(itemKey)
	                    .updateExpression("SET " + listAttributeKeyName + " = :val")
	                    .expressionAttributeValues(attributeValues)
	                    .build();

	            dynamodbClient.updateItem(updateItemRequest);
	            
			} else {
				// Exiting entry for this detected text exists./ Update the same
				System.out.println("Existing entry, lets add to it");
	            Map<String, AttributeValue> itemKey = new HashMap<>();
	            itemKey.put(pkKeyName, AttributeValue.builder().s(detectedTextToProcess).build());
	            
	         // New values to append
	            AttributeValue newValues = AttributeValue.builder()
	                    .l(Arrays.asList(AttributeValue.builder().s(imageId).build(),
                                		AttributeValue.builder().s(imageUrl).build()))
	                    .build();
	            
	            // Setting up the update expression to append newValues to the list
	            String updateExpression = "SET " + listAttributeKeyName + " = list_append(" + listAttributeKeyName + ", :val)";

	            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
	            expressionAttributeValues.put(":val", newValues);

	            UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
	                    .tableName(ddbTableName)
	                    .key(itemKey)
	                    .updateExpression(updateExpression)
	                    .expressionAttributeValues(expressionAttributeValues)
	                    .build();

	            dynamodbClient.updateItem(updateItemRequest);


			}
		}
		
        
		
	}
	

}
