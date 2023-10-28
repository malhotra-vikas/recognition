package com.treenity.image.recognition.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
	
	
    public static void putItemInTable(DynamoDbClient ddb,
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
    
}
