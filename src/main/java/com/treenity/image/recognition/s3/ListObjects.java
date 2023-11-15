//snippet-sourcedescription:[ListObjects.java demonstrates how to list objects located in a given Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon S3]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.treenity.image.recognition.s3;

// snippet-start:[s3.java2.list_objects.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.util.Optional;
import java.util.List;
// snippet-end:[s3.java2.list_objects.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ListObjects {


    // snippet-start:[s3.java2.list_objects.main]
    public List<S3Object> listBucketObjects(S3Client s3, String bucketName ) {

    	List<S3Object> objects = null;
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(bucketName)
                .build();

            ListObjectsResponse res = s3.listObjects(listObjects);
            objects = res.contents();
            String URL = "";
            String key;
            String region = "us-east-2";
            
            for (S3Object myValue : objects) {
            	key = myValue.key();
            	URL = "https://" + "s3." + region + ".amazonaws.com/" + bucketName +"/" + key;

                System.out.println("\n In LIst Objects: The name of the key is " + key);
                System.out.println("\n In LIst Objects: The URL is " + URL);
            }

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return objects;
    }

    public Optional<S3Object> getObjectByKey(S3Client s3, String bucketName, String objectKey) {

    	try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

            s3.getObject(getObjectRequest);

            // If the object is successfully retrieved, return an Optional of it
            return Optional.of(S3Object.builder()
                    .key(objectKey)
                    // Additional attributes can be set here if needed
                    .build());
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            // Consider a better error handling strategy for your use case
        }
        return Optional.empty();
    }
    
    //convert bytes to kbs.
    private static long calKb(Long val) {
        return val/1024;
    }
   // snippet-end:[s3.java2.list_objects.main]
}
