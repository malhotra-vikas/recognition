package com.treenity.image.ddb;

import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.treenity.image.recognition.utils.DDBHandler;
import com.treenity.image.recognition.utils.DependencyFactory;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;



public class DDBCrud {

	public DDBCrud() {

	}
	
	public void createItem(String tableName, Map<String, AttributeValue> item) {
        DynamoDbClient dynamodbClient = DependencyFactory.ddbClient();   

        PutItemRequest request = PutItemRequest.builder()
	            .tableName(tableName)
	            .item(item)
	            .build();

	    try {
	        dynamodbClient.putItem(request);
	        System.out.println("Item created");
	    } catch (DynamoDbException e) {
	        System.err.println("Create item failed: " + e.getMessage());
	    }
	}
	
	public void persistNoConfidenceImage(String keyToUpdate) {
        DynamoDbClient dynamodbClient = DependencyFactory.ddbClient();
        String ddbTableName = "noConfidenceImages.1";

        HashMap<String, String> attributesHashMap = new HashMap<>();
        attributesHashMap.put("imageid", keyToUpdate);

        DDBHandler ddbHandler = new DDBHandler();
        ddbHandler.putItemInTable(dynamodbClient, ddbTableName, attributesHashMap);
	}
	
	
	public Map<String, AttributeValue> readItem(String tableName, Map<String, AttributeValue> keyToGet, String columnsToRead) {
        DynamoDbClient dynamodbClient = DependencyFactory.ddbClient(); 
        

        // Build the GetItemRequest with conditional projectionExpression
        GetItemRequest.Builder requestBuilder = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(tableName);

        if (columnsToRead != null && !columnsToRead.isEmpty()) {
            requestBuilder.projectionExpression(columnsToRead);
        }

        GetItemRequest request = requestBuilder.build();

	    Map<String, AttributeValue> returnedItem = null;
	    try {
	        returnedItem = dynamodbClient.getItem(request).item();
	        if (returnedItem != null) {
	            System.out.println("Item found");
	        } else {
	            System.out.println("Item not found");
	        }
	    } catch (DynamoDbException e) {
	        System.err.println("Read item failed: " + e.getMessage());
	    }
	    return returnedItem;
	}
	
	public void updateItem(String tableName, Map<String, AttributeValue> key, Map<String, AttributeValueUpdate> updatedValues) {
        DynamoDbClient dynamodbClient = DependencyFactory.ddbClient();   

	    UpdateItemRequest request = UpdateItemRequest.builder()
	            .tableName(tableName)
	            .key(key)
	            .attributeUpdates(updatedValues)
	            .build();

	    try {
	        dynamodbClient.updateItem(request);
	        System.out.println("Item updated");
	    } catch (DynamoDbException e) {
	        System.err.println("Update item failed: " + e.getMessage());
	    }
	}
	
	public void deleteItem(String tableName, Map<String, AttributeValue> key) {
        DynamoDbClient dynamodbClient = DependencyFactory.ddbClient();   

	    DeleteItemRequest request = DeleteItemRequest.builder()
	            .tableName(tableName)
	            .key(key)
	            .build();

	    try {
	        dynamodbClient.deleteItem(request);
	        System.out.println("Item deleted");
	    } catch (DynamoDbException e) {
	        System.err.println("Delete item failed: " + e.getMessage());
	    }
	}
	
	public void scanTable(String tableName) {
        DynamoDbClient dynamodbClient = DependencyFactory.ddbClient();   
        //String ddbTableName = DependencyFactory.getDdbTableName_default()+"."+DependencyFactory.getCompanyID();
        
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();
        
        try {
            ScanResponse response = dynamodbClient.scan(scanRequest);
            for (Map<String, AttributeValue> item : response.items()){
                // Print out the retrieved items (for demonstration purposes)
                for (String key : item.keySet()) {
                	if (key.equals("imageid") || key.equals("hiconfidence-image-text") || key.equals("imageurl")) {
                		System.out.println(key + ": " + item.get(key));
                	}
                    
                }
                System.out.println("--------------");  // Separator between items for clarity
            }

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
	}
	
	public ArrayList<String> fetchAllKeys(String tableName, String keyColumn) {

        // Create the DynamoDbClient object
        DynamoDbClient dynamodbClient = DependencyFactory.ddbClient();   
        ArrayList<String> scannedKeys = new ArrayList<String> ();
        Map<String, AttributeValue> lastEvaluatedKey = null;
        int count = 0;
        AttributeValue attrValue = null;

        
        do {
            try {
                ScanRequest scanRequest = ScanRequest.builder()
                        .tableName(tableName)
                        .exclusiveStartKey(lastEvaluatedKey)
                        .projectionExpression(keyColumn)
                        .build();

                ScanResponse scanResponse = dynamodbClient.scan(scanRequest);
                List<Map<String, AttributeValue>> items = scanResponse.items();

                // Process each item
                for (Map<String, AttributeValue> item : items) {
                    // Here you can handle each item as you need
                    //System.out.println(item);
                    count ++;
                    attrValue = item.get(keyColumn);
                    if (attrValue != null && attrValue.s() != null) {
                        String imageId = attrValue.s();
                        scannedKeys.add(imageId);
                        //System.out.println(imageId);

                    }
                }

                lastEvaluatedKey = scanResponse.lastEvaluatedKey();
                //System.out.println(count);
                //System.out.println(lastEvaluatedKey);
                System.out.println("scannedKeys.size  :" + scannedKeys.size());

            } catch (DynamoDbException e) {
                System.err.println(e.getMessage());
            }
        } while (!lastEvaluatedKey.isEmpty());
        return scannedKeys;
    }

}
