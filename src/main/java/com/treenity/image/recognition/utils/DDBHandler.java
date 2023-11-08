package com.treenity.image.recognition.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.treenity.image.ddb.DDBCrud;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;


public class DDBHandler {
    private final DynamoDbClient dynamodbClient;

    public DDBHandler() {
        dynamodbClient = DependencyFactory.ddbClient();
    }
	
    public Map<String, AttributeValue> fetchDDBEntry(String keyValue) {
    	
    	String tableName = "recognizedText.1"; 
    	String columnsToGet = "imageArtifacts"; 
        String keyColumn = "detectedText"; 

    	DDBCrud crud = new DDBCrud();

        // Creating the DynamoDbClient object
        DynamoDbClient ddb = DependencyFactory.ddbClient();   
    	Map<String, AttributeValue> returnedItem;


        // Construct the key with which to query
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();

        keyToGet.put(keyColumn, AttributeValue.builder()
                .s(keyValue) // .s for string type attributes, you can use .n for number types, etc.
                .build());
        
    	returnedItem = crud.readItem(tableName, keyToGet, columnsToGet);

        
        // Close the DynamoDbClient
        ddb.close();
        
        return returnedItem;
        
    }
    
    public void putItemInTable(DynamoDbClient ddb,
            String tableName,
            HashMap<String, String> attributesHashMap) {
    	
    	
        HashMap<String,AttributeValue> itemValues = new HashMap<>();
        String key;
        String keyVal;
        
        Set<String> keysSet = attributesHashMap.keySet();
        Iterator<String> keysItertor = keysSet.iterator();
        
       
        while (keysItertor.hasNext()) {
        	key = (String) keysItertor.next();
        	keyVal = (String) attributesHashMap.get(key);
        	
            itemValues.put(key, AttributeValue.builder().s(keyVal).build());
        }

        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(itemValues)
            .build();

        try {
            PutItemResponse response = ddb.putItem(request);
            System.out.println(tableName +" was successfully updated. The request id is "+response.responseMetadata().requestId());

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    
    
    public void putItemInTable(DynamoDbClient ddb,
            String tableName, String detectedText, String imageId, String imageUrl, HashMap<String, String> attributesHashMap) {
    	
    	
        HashMap<String,AttributeValue> itemValues = new HashMap<>();
        String key;
        String keyVal;
        
        Set<String> keysSet = attributesHashMap.keySet();
        Iterator<String> keysItertor = keysSet.iterator();
        
       
        while (keysItertor.hasNext()) {
        	key = (String) keysItertor.next();
        	keyVal = (String) attributesHashMap.get(key);
        	
            itemValues.put(key, AttributeValue.builder().s(keyVal).build());
        }

        PutItemRequest request = PutItemRequest.builder()
            .tableName(tableName)
            .item(itemValues)
            .build();

        try {
            PutItemResponse response = ddb.putItem(request);
            System.out.println(tableName +" was successfully updated. The request id is "+response.responseMetadata().requestId());

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
