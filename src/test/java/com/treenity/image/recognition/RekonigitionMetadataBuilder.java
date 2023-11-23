package com.treenity.image.recognition;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

       

/**
 * Lambda function entry point. You can change to use other pojo type or implement
 * a different RequestHandler.
 *
 * @see <a href=https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html>Lambda Java Handler</a> for more information
 */
public class RekonigitionMetadataBuilder {
    private static final Logger logger = LoggerFactory.getLogger(RekonigitionMetadataBuilder.class);


    public static void main(String... args) {
        logger.info("Application starts");
        String S3bucket = DependencyFactory.getS3Bucket_default()+"."+DependencyFactory.getCompanyID();
        
        S3Client s3Client = DependencyFactory.s3Client();
        GetObjectUrl getObjectURl = new GetObjectUrl();

        S3Handler s3Handler = new S3Handler();
        
// return all images from the bucket
        List<S3Object> s3Objects = s3Handler.fetchAllObjects(s3Client, S3bucket);
        
// Iterate over S3 Objects, and process
        String keyToProcess;
        S3Object s3ObjectToProcess;
        URL urlToProcess;
        String detectedTextToProcess;
        String highConfidenceText;
        List<ParsedText> parsedTextList;
        ParsedText text;
        
        List<ParsedText> highConfidenceParsedTextList;

    	Gson gson = new GsonBuilder().create();


        
        for (int i=0; i<s3Objects.size(); i++) {
        	highConfidenceParsedTextList = new ArrayList<ParsedText>();
        	//highConfidenceParsedLabelsList = new ArrayList<ParsedLabel>();
        	
        	s3ObjectToProcess = (S3Object) s3Objects.get(i);
        	keyToProcess = s3ObjectToProcess.key();
        	urlToProcess = getObjectURl.getURL(s3Client, S3bucket, keyToProcess);
        	
        	System.out.println("Iterating - Key - " + keyToProcess);
        	System.out.println("Iterating - URL - " + urlToProcess);

// Build Rekognition Client
        	RekognitionClient rekClient = DependencyFactory.getRekognitionClient();
            RekognitionHandler rekognitionHandler = new RekognitionHandler();

// Detect Text

            detectedTextToProcess = rekognitionHandler.detectText(rekClient, S3bucket, "", keyToProcess);
            //System.out.println("Iterating Detected Text - Text - " + detectedTextToProcess);
            
            JsonFilter jsonFilter = new JsonFilter();
            int confidenceToFilter = 95;
            
            highConfidenceText = jsonFilter.filterJsonByConfidence(confidenceToFilter, detectedTextToProcess);
        
            
// DDB Operations
            DynamoDbClient dynamodbClient = DependencyFactory.ddbClient();   
            String ddbTableName = DependencyFactory.getDdbTableName_default()+"."+DependencyFactory.getCompanyID();
            HashMap<String, String> attributesHashMap = new HashMap<>();
            attributesHashMap.put(DependencyFactory.getDdbPK_key(), keyToProcess);
            attributesHashMap.put(DependencyFactory.getDdbImageURL_key(), urlToProcess.toString());
            attributesHashMap.put(DependencyFactory.getDetectedText_key(), detectedTextToProcess);
            //attributesHashMap.put(DependencyFactory.getDetectedLabel_key(), detectedLabelsToProcess);
            attributesHashMap.put(DependencyFactory.getHighConfidemnceText_key(), highConfidenceText);
            //attributesHashMap.put(DependencyFactory.getHighConfidemnceLabel_key(), highConfidenceLabelsToProcess);

            DDBHandler ddbHandler = new DDBHandler();
            ddbHandler.putItemInTable(dynamodbClient, ddbTableName, attributesHashMap);
            
// Clean S3, move objects to another Bucket
            
            

        }

        
        logger.info("Application ends");
   

    }
    
}
