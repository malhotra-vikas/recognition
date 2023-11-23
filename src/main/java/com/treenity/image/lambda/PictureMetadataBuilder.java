package com.treenity.image.lambda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.treenity.image.recognition.utils.DDBHandler;
import com.treenity.image.recognition.utils.DependencyFactory;
import com.treenity.image.recognition.utils.JsonFilter;
import com.treenity.image.recognition.utils.ParsedText;
import com.treenity.image.recognition.utils.RekognitionHandler;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

public class PictureMetadataBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(PictureMetadataBuilder.class);


    public void generateMetadata(String eventName, String photographerId, String imageId) {
        logger.info("Application starts");
        String S3bucket = DependencyFactory.getS3Bucket_default()+"."+DependencyFactory.getCompanyID();
        String folderStructure = eventName+"/"+photographerId+"/";
        
        //S3bucket = S3bucket+"/"+eventName+"/"+photographerId+"/"+imageId;
       
        
// Iterate over S3 Objects, and process
        String keyToProcess;
        String detectedTextToProcess;
        String highConfidenceText;
        List<ParsedText> parsedTextList;
        
        List<ParsedText> highConfidenceParsedTextList;

    	String URL;
        String region = "us-east-2";


        //for (int i=0; i<s3Objects.size(); i++) {
        highConfidenceParsedTextList = new ArrayList<ParsedText>();
        	
        keyToProcess = imageId;
        	
        URL = "https://" + "s3." + region + ".amazonaws.com/" + S3bucket +"/"+eventName+"/"+photographerId+"/"+imageId;
        	
       	System.out.println("Iterating - Key - " + keyToProcess);
       	System.out.println("Iterating - URL - " + URL);

// Build Rekognition Client
       	RekognitionClient rekClient = DependencyFactory.getRekognitionClient();
        RekognitionHandler rekognitionHandler = new RekognitionHandler();
// Detect Text

        
        detectedTextToProcess = rekognitionHandler.detectText(rekClient, S3bucket, folderStructure, keyToProcess);
            
        JsonFilter jsonFilter = new JsonFilter();
        int confidenceToFilter = 95;
            
        highConfidenceText = jsonFilter.filterJsonByConfidence(confidenceToFilter, detectedTextToProcess);
        
            
// DDB Operations
        DynamoDbClient dynamodbClient = DependencyFactory.ddbClient();   
        String ddbTableName = DependencyFactory.getDdbTableName_default()+"."+DependencyFactory.getCompanyID();
        HashMap<String, String> attributesHashMap = new HashMap<>();
        attributesHashMap.put(DependencyFactory.getDdbPK_key(), eventName+":"+photographerId+":"+imageId);
        attributesHashMap.put(DependencyFactory.getDdbImageURL_key(), URL);
        attributesHashMap.put(DependencyFactory.getDetectedText_key(), detectedTextToProcess);
            //attributesHashMap.put(DependencyFactory.getDetectedLabel_key(), detectedLabelsToProcess);
        attributesHashMap.put(DependencyFactory.getHighConfidemnceText_key(), highConfidenceText);
            //attributesHashMap.put(DependencyFactory.getHighConfidemnceLabel_key(), highConfidenceLabelsToProcess);

        DDBHandler ddbHandler = new DDBHandler();
        ddbHandler.putItemInTable(dynamodbClient, ddbTableName, attributesHashMap);
        
        logger.info("Application ends");
    	System.out.println("S3 Objects Processed");
        logger.debug(" DEBUG Application ends");


    }

}
