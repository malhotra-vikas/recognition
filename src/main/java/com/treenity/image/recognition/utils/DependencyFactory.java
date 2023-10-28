
package com.treenity.image.recognition.utils;

import static software.amazon.awssdk.transfer.s3.SizeConstant.MB;

import com.impgo.demoappsp.examples.AWSRekonigitionMaster;

import lombok.Data;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;
import software.amazon.awssdk.services.rekognition.RekognitionClient;


/**
 * The module containing all dependencies required by the {@link AWSRekonigitionMaster}.
 */
public class DependencyFactory {

    public static final String getS3Bucket_default() {
		return s3Bucket_default;
	}

	public static final void setS3Bucket_default(String s3Bucket_default) {
		DependencyFactory.s3Bucket_default = s3Bucket_default;
	}

	public static final String getS3bucket_processed_default() {
		return s3bucket_processed_default;
	}

	public static final void setS3bucket_processed_default(String s3bucket_processed_default) {
		DependencyFactory.s3bucket_processed_default = s3bucket_processed_default;
	}

	public static final void setDdbTableName_default(String ddbTableName_default) {
		DependencyFactory.ddbTableName_default = ddbTableName_default;
	}

	public static final void setCompanyID(String companyID) {
		DependencyFactory.companyID = companyID;
	}

	public static final void setDdbPK_key(String ddbPK_key) {
		DependencyFactory.ddbPK_key = ddbPK_key;
	}

	public static final void setDdbImageURL_key(String ddbImageURL_key) {
		DependencyFactory.ddbImageURL_key = ddbImageURL_key;
	}

	public static final void setDetectedText_key(String detectedText_key) {
		DependencyFactory.detectedText_key = detectedText_key;
	}

	public static final void setDetectedLabel_key(String detectedLabel_key) {
		DependencyFactory.detectedLabel_key = detectedLabel_key;
	}

	public static final void setRegion(Region region) {
		DependencyFactory.region = region;
	}


	private static String ddbTableName_default = "ImageMetadata";
    private static String companyID = "1";
    private static String ddbPK_key = "imageid";
    private static String ddbImageURL_key = "imageurl";
    private static String detectedText_key = "detectedtext";
    private static String detectedLabel_key = "detectedlabel";
    private static String highConfidemnceLabel_key = "hiconfidence-image-label";
    private static String highConfidemnceText_key = "hiconfidence-image-text";


    private static String s3Bucket_default = "mediastore";
    private static String s3bucket_processed_default = "mediastore.processed";

    private static Region region = Region.US_EAST_2;
    
    private DependencyFactory() {}

    /**
     * @return an instance of S3Client
     */
    public static S3Client s3Client() {
        return S3Client.builder()
                       .credentialsProvider(ProfileCredentialsProvider.create("default"))
                       .region(Region.US_EAST_2)
                       .httpClientBuilder(UrlConnectionHttpClient.builder())
                       .build();
    }
    
    public static S3TransferManager createDefaultTm() {
        // snippet-start:[s3.tm.java2.s3clientfactory.create_default_tm]
        S3TransferManager transferManager = S3TransferManager.create();
        // snippet-end:[s3.tm.java2.s3clientfactory.create_default_tm]
        return transferManager;
    }
    
    public static S3TransferManager createCustonTm(){
        // snippet-start:[s3.tm.java2.s3clientfactory.create_custom_tm]
        S3AsyncClient s3AsyncClient =
            S3AsyncClient.crtBuilder()
            	.credentialsProvider(ProfileCredentialsProvider.create("default"))
            	.region(Region.US_EAST_2)
                .targetThroughputInGbps(20.0)
                .minimumPartSizeInBytes(8 * MB)
                .build();

        S3TransferManager transferManager =
            S3TransferManager.builder()
                .s3Client(s3AsyncClient)
                .build();
        // snippet-end:[s3.tm.java2.s3clientfactory.create_custom_tm]
        return transferManager;
    }

    public static DynamoDbClient ddbClient() {
    	return DynamoDbClient.builder()
    			.credentialsProvider(ProfileCredentialsProvider.create("default"))
    			.region(Region.US_EAST_2)
                .httpClientBuilder(UrlConnectionHttpClient.builder())
    			.build();
    }
    
    public static RekognitionClient getRekognitionClient() {
    	return RekognitionClient.builder()
    			.credentialsProvider(ProfileCredentialsProvider.create("default"))
    			.region(Region.US_EAST_2)
    			.httpClientBuilder(UrlConnectionHttpClient.builder())
    			.build();
    		}

	public static final String getDdbTableName_default() {
		return ddbTableName_default;
	}

	public static final String getCompanyID() {
		return companyID;
	}

	public static final String getDdbPK_key() {
		return ddbPK_key;
	}

	public static final String getDdbImageURL_key() {
		return ddbImageURL_key;
	}

	public static final String getDetectedText_key() {
		return detectedText_key;
	}

	public static final String getDetectedLabel_key() {
		return detectedLabel_key;
	}


	public static final Region getRegion() {
		return region;
	}

	public static String getHighConfidemnceLabel_key() {
		return highConfidemnceLabel_key;
	}

	public static void setHighConfidemnceLabel_key(String highConfidemnceLabel_key) {
		DependencyFactory.highConfidemnceLabel_key = highConfidemnceLabel_key;
	}

	public static String getHighConfidemnceText_key() {
		return highConfidemnceText_key;
	}

	public static void setHighConfidemnceText_key(String highConfidemnceText_key) {
		DependencyFactory.highConfidemnceText_key = highConfidemnceText_key;
	}
    
    
    
        
}
