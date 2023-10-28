// snippet-sourcedescription:[DetectText.java demonstrates how to display words that were detected in an image.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Rekognition]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.treenity.image.recognition;

import java.lang.reflect.Type;
import java.util.List;
// snippet-end:[rekognition.java2.detect_text.import]

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.treenity.image.recognition.utils.*;

import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectTextRequest;
import software.amazon.awssdk.services.rekognition.model.DetectTextResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.S3Object;
import software.amazon.awssdk.services.rekognition.model.TextDetection;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DetectText {
	
    public DetectText() {
	}

    String textJSON = null;
    


    public List<ParsedText> buildHighConfidenceText(String textJsonString) {
    	
    	Gson gson = new GsonBuilder().create();
        Type parsedTextListType = new TypeToken<List<ParsedText>>() {}.getType();
        List<ParsedText> parsedTextList = gson.fromJson(textJsonString, parsedTextListType);
        
    	return parsedTextList;
    	
    }
    
    public String detectTextLabels(RekognitionClient rekClient, String bucket, String image) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
        	
            S3Object s3Object = S3Object.builder()
                    .bucket(bucket)
                    .name(image)
                    .build();

                Image souImage = Image.builder()
                    .s3Object(s3Object)
                    .build();

            DetectTextRequest textRequest = DetectTextRequest.builder()
                .image(souImage)
                .build();

            DetectTextResponse textResponse = rekClient.detectText(textRequest);
            List<TextDetection> textCollection = textResponse.textDetections();
            textJSON = gson.toJson(textCollection);


        } catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return textJSON;

    }
}
