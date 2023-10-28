// snippet-sourcedescription:[DetectLabels.java demonstrates how to capture labels (like water and mountains) in a given image.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Rekognition]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.treenity.image.recognition;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
// snippet-end:[rekognition.java2.detect_labels.import]

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.treenity.image.recognition.utils.*;

import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Label;
import software.amazon.awssdk.services.rekognition.model.RekognitionException;
import software.amazon.awssdk.services.rekognition.model.S3Object;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DetectLabels {

    public List<ParsedLabel> buildHighConfidenceLabels(String labelJsonString) {
    	
        Gson gson = new GsonBuilder().create();
        Type parsedLabelListType = new TypeToken<List<ParsedLabel>>() {}.getType();
        List<ParsedLabel> parsedLabelList = gson.fromJson(labelJsonString, parsedLabelListType);
        
        
        //Type collectionType = new TypeToken<Collection<ChannelSearchEnum>>(){}.getType();
        //Collection<ChannelSearchEnum> enums = gson.fromJson(yourJson, collectionType);

        
    	return parsedLabelList;
    	
    }

    // snippet-start:[rekognition.java2.detect_labels.main]
    public String detectImageLabels(RekognitionClient rekClient, String bucket, String sourceImage) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String textJSON = null;


        try {
        	S3Object s3Object = S3Object.builder()
                    .bucket(bucket)
                    .name(sourceImage)
                    .build();

                Image souImage = Image.builder()
                    .s3Object(s3Object)
                    .build();

            DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
                .image(souImage)
                .maxLabels(10)
                .build();

            DetectLabelsResponse labelsResponse = rekClient.detectLabels(detectLabelsRequest);
            List<Label> labels = labelsResponse.labels();
            textJSON = gson.toJson(labels);

        } catch (RekognitionException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return textJSON;
    }
    // snippet-end:[rekognition.java2.detect_labels.main]
}
