package com.treenity.image.recognition;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.math.NumberUtils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.treenity.image.ddb.DDBCrud;
import com.treenity.image.recognition.utils.DependencyFactory;
import com.treenity.image.recognition.utils.ImageKeyUtils;
import com.treenity.image.recognition.utils.NumberUtillity;

public class DynamoDBACtionTest {

    public static void main(String[] args) {
    	DynamoDBACtionTest dynamoDBACtionTest = new DynamoDBACtionTest();
    	
    	//dynamoDBACtionTest.testRead();
    	
    	System.out.print("________-----------------------------_______________");
    	
    	dynamoDBACtionTest.testReadAllKeys();
    }
    
    
    public void testReadAllKeys() {
    	String tableName = "ImageMetadata.1";
        String keyColumn = "imageid"; // Replace with your key column name
        ArrayList<String> scannedKeys = new ArrayList<>();

        
    	DDBCrud crud = new DDBCrud();
    	ImageKeyUtils imageKeyUtils = new ImageKeyUtils();

    	// Ideally this comes from a stream or a queue that has all Image Keys
    	
    	//scannedKeys = crud.fetchAllKeys(tableName, keyColumn);
		System.out.println("scannedKeys size : " + scannedKeys.size());

    	String keyToRead;
    	ArrayList<String> detectedTextList = null;
    	
    	
    	for (int i=0; i<scannedKeys.size(); i++) {
    		keyToRead = scannedKeys.get(i);
    		detectedTextList = new ArrayList<String>();
    		
    		System.out.println("Key To read : " + keyToRead);
    		    		
//    		hiconfidenceImageText = imageKeyUtils.readHiconfidenceImageText(keyToRead);
    		imageKeyUtils = testRead(keyToRead);
    		
            ObjectMapper objectMapper = new ObjectMapper();

            // Assuming the JSON is an array of objects, you can define a suitable class to deserialize into,
            // or simply use a Map if the structure is not fixed.
            // For the purpose of this example, let's use a List<Map<String, Object>>.
            try {
                // Parse the JSON string into a List of Maps
                List<Map<String, Object>> hiconfidenceImageTextList = objectMapper.readValue(
                		imageKeyUtils.getHighConfidenceDetectedTextList(),
                    new TypeReference<List<Map<String, Object>>>() {}
                );
                
                // Case where there was no high confidence recognition for a number
                if (hiconfidenceImageTextList.size() == 0) {
                    System.out.println("Saving No Text Detected File");
                	crud.persistNoConfidenceImage(keyToRead);
                }

                String detectedText;
                // Iterate over the list and read the desired values
                for (Map<String, Object> textEntry : hiconfidenceImageTextList) {
                    detectedText = (String) textEntry.get("detectedText");
                    System.out.println("detected text :" + detectedText + ":");

                    if (NumberUtillity.isNumeric(detectedText)) {
                    	detectedTextList.add(detectedText);
                        System.out.println("Number only detected text : " + detectedText);

                    }
                }
                imageKeyUtils.setDetectedTextList(detectedTextList);
                
                //System.out.println(imageKeyUtils.getImageId());
                //System.out.println(imageKeyUtils.getImageURL());
                System.out.println("Number only detected text Size : " + imageKeyUtils.getDetectedTextList().size());
                
                imageKeyUtils.transformAndPersistToDDB(imageKeyUtils, "eventName");
                

            } catch (IOException e) {
                e.printStackTrace();
            }

    	}
        
        
    }
    
    public ImageKeyUtils testRead(String keyValue) {
    	String tableName = "ImageMetadata.1";
        String keyColumn = "imageid"; // Replace with your key column name
        String columnsToGet = "imageid, hiconfidenceImageText, imageurl"; // Comma-separated string of attributes to retrieve
        
    	DDBCrud crud = new DDBCrud();
    	ImageKeyUtils imageKeyUtils = new ImageKeyUtils();

        // Creating the DynamoDbClient object
        DynamoDbClient ddb = DependencyFactory.ddbClient();   
    	Map<String, AttributeValue> returnedItem;


        // Construct the key with which to query
        HashMap<String, AttributeValue> keyToGet = new HashMap<>();

        keyToGet.put(keyColumn, AttributeValue.builder()
                .s(keyValue) // .s for string type attributes, you can use .n for number types, etc.
                .build());
        
    	returnedItem = crud.readItem(tableName, keyToGet, columnsToGet);
    	imageKeyUtils.setImageId(returnedItem.get("imageid").s());
    	imageKeyUtils.setImageURL(returnedItem.get("imageurl").s());
    	imageKeyUtils.setHighConfidenceDetectedTextList(returnedItem.get("hiconfidenceImageText").s());

        
        // Close the DynamoDbClient
        ddb.close();
        
        return imageKeyUtils;
        
    }
}
