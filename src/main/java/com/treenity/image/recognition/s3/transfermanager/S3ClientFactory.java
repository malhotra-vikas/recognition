//snippet-sourcedescription:[S3ClientFactory.java demonstrates how to create instances of the Amazon Simple Storage Service (Amazon S3) TransferManager.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon S3]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.treenity.image.recognition.s3.transfermanager;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import static software.amazon.awssdk.transfer.s3.SizeConstant.MB;

import com.treenity.image.recognition.utils.DependencyFactory;


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class S3ClientFactory {
    public static final S3TransferManager transferManager;
    public static final S3Client s3Client;


    static {
        
        s3Client = DependencyFactory.s3Client();
        transferManager = DependencyFactory.createDefaultTm();
    }
}
