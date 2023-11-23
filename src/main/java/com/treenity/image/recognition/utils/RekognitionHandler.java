package com.treenity.image.recognition.utils;


import java.util.Collection;
import java.util.List;

import com.treenity.image.recognition.*;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;


public class RekognitionHandler {
    private final RekognitionClient rekClient;

    public RekognitionHandler() {
    	rekClient = DependencyFactory.getRekognitionClient();
    }

    String bucket = DependencyFactory.getS3Bucket_default()+"."+DependencyFactory.getCompanyID();
    //String image = "Lake.png";
    Region region = DependencyFactory.getRegion();
    
   
    
    public String detectText(RekognitionClient rekClient, String bucket, String folderStructure, String image) {
    	DetectText detectText = new DetectText();
    	return detectText.detectTextLabels(rekClient, bucket, folderStructure, image);
    }
    
    public List<ParsedText> buildHighConfidenceText(String parsedText) {
    	DetectText detectText = new DetectText();
    	return detectText.buildHighConfidenceText(parsedText);
    }
    
    public List<ParsedLabel> buildHighConfidenceLabel(String parsedLabel) {
    	DetectLabels detectLabels = new DetectLabels();
    	return detectLabels.buildHighConfidenceLabels(parsedLabel);
    }
    
    public String detectLabels(RekognitionClient rekClient, String bucket, String image) {
    	DetectLabels detectLabels = new DetectLabels();
    	return detectLabels.detectImageLabels(rekClient, bucket, image);
    	
    }
    
    

}
