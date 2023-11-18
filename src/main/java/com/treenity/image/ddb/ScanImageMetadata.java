package com.treenity.image.ddb;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;
import java.util.Map;

import com.treenity.image.recognition.utils.DependencyFactory;


public class ScanImageMetadata {
	
	/*
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
                	if (key.equals("imageid") || key.equals("hiconfidenceImageText") || key.equals("imageurl")) {
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
	*/
    
}
