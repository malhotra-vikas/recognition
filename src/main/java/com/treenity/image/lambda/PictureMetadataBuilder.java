package com.treenity.image.lambda;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.treenity.image.recognition.s3.GetObjectUrl;
import com.treenity.image.recognition.utils.DDBHandler;
import com.treenity.image.recognition.utils.DependencyFactory;
import com.treenity.image.recognition.utils.JsonFilter;
import com.treenity.image.recognition.utils.ParsedText;
import com.treenity.image.recognition.utils.RekognitionHandler;
import com.treenity.image.recognition.utils.S3Handler;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Object;

public class PictureMetadataBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(PictureMetadataBuilder.class);


    public void generateMetadata(String s3ObjectName) {
        logger.info("Application starts");
        String S3bucket = DependencyFactory.getS3Bucket_default()+"."+DependencyFactory.getCompanyID();
        
        S3Client s3Client = DependencyFactory.s3Client();
        GetObjectUrl getObjectURl = new GetObjectUrl();

        S3Handler s3Handler = new S3Handler();
        
// return all images from the bucket
        Optional<S3Object> s3Object = s3Handler.fetchObjectByKey(s3Client, S3bucket, s3ObjectName);
        
// Iterate over S3 Objects, and process
        String keyToProcess;
        Optional<S3Object> s3ObjectToProcess = s3Object;
        //URL urlToProcess;
        String detectedTextToProcess;
        String highConfidenceText;
        List<ParsedText> parsedTextList;
        ParsedText text;
        
        List<ParsedText> highConfidenceParsedTextList;

    	Gson gson = new GsonBuilder().create();
    	String URL;
        String region = "us-east-2";


        //for (int i=0; i<s3Objects.size(); i++) {
        highConfidenceParsedTextList = new ArrayList<ParsedText>();
        	
        keyToProcess = s3ObjectName;
        	
        URL = "https://" + "s3." + region + ".amazonaws.com/" + S3bucket +"/" + keyToProcess;
        	
       	System.out.println("Iterating - Key - " + keyToProcess);
       	System.out.println("Iterating - URL - " + URL);

// Build Rekognition Client
       	RekognitionClient rekClient = DependencyFactory.getRekognitionClient();
        RekognitionHandler rekognitionHandler = new RekognitionHandler();
// Detect Text

        detectedTextToProcess = rekognitionHandler.detectText(rekClient, S3bucket, keyToProcess);
            
        JsonFilter jsonFilter = new JsonFilter();
        int confidenceToFilter = 95;
            
        highConfidenceText = jsonFilter.filterJsonByConfidence(confidenceToFilter, detectedTextToProcess);
        
            
// DDB Operations
        DynamoDbClient dynamodbClient = DependencyFactory.ddbClient();   
        String ddbTableName = DependencyFactory.getDdbTableName_default()+"."+DependencyFactory.getCompanyID();
        HashMap<String, String> attributesHashMap = new HashMap<>();
        attributesHashMap.put(DependencyFactory.getDdbPK_key(), keyToProcess);
        attributesHashMap.put(DependencyFactory.getDdbImageURL_key(), URL);
        attributesHashMap.put(DependencyFactory.getDetectedText_key(), detectedTextToProcess);
            //attributesHashMap.put(DependencyFactory.getDetectedLabel_key(), detectedLabelsToProcess);
        attributesHashMap.put(DependencyFactory.getHighConfidemnceText_key(), highConfidenceText);
            //attributesHashMap.put(DependencyFactory.getHighConfidemnceLabel_key(), highConfidenceLabelsToProcess);

        DDBHandler ddbHandler = new DDBHandler();
        ddbHandler.putItemInTable(dynamodbClient, ddbTableName, attributesHashMap);
            
        //}

        
        logger.info("Application ends");
    	System.out.println("S3 Objects Processed");
        logger.debug(" DEBUG Application ends");


    }

}
