package com.treenity.image.recognition.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.treenity.image.ddb.DDBCrud;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

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
        System.out.println(size);

		for (int i=0; i<size; i++) {
			fetchedEntry = null;
			
			detectedTextToProcess = detectedTextList.get(i);
			System.out.println(detectedTextToProcess);

			
			// Fetch an existing Entry for the detectedText
			fetchedEntry = ddbHandler.fetchDDBEntry(detectedTextToProcess);
			
			System.out.println(fetchedEntry);
			System.out.println(fetchedEntry.size());

			// If existing entry exist
			
			// If existing entry does not exist 
			if (fetchedEntry == null || fetchedEntry.size() == 0)  {
				// Exiting entry for this detected text does not exists./ Create a new one
	            String ddbTableName = "recognizedText.1";
	            HashMap<String, String> attributesHashMap = new HashMap<>();
	            attributesHashMap.put("detectedText", detectedTextToProcess);

	            //attributesHashMap.put(DependencyFactory.getDetectedLabel_key(), detectedLabelsToProcess);
	            attributesHashMap.put("imageArtifacts", imageId + " : " + imageUrl);
	            //attributesHashMap.put(DependencyFactory.getHighConfidemnceLabel_key(), highConfidenceLabelsToProcess);

	            ddbHandler.putItemInTable(dynamodbClient, ddbTableName, attributesHashMap);				
			} else {
				// Exiting entry for this detected text exists./ Update the same

			}
		}
		
        
		
	}
	

}
