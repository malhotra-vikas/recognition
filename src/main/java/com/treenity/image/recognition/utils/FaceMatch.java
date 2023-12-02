package com.treenity.image.recognition.utils;

import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.core.SdkBytes;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FaceMatch {
    public static void main(String[] args) {
        // Set up the Rekognition client
        Region region = Region.US_EAST_1; // Replace with your desired AWS region
        RekognitionClient rekognitionClient = RekognitionClient.builder()
                .region(region)
                .build();

        // Specify the source image file (seed face)
        String sourceImageFilePath = "path/to/source/image.jpg"; // Replace with the path to the source image file

        // Load the source image from the file
        File sourceImageFile = new File(sourceImageFilePath);
        SdkBytes sourceImageBytes;

        try (InputStream sourceImageInputStream = new FileInputStream(sourceImageFile)) {
            sourceImageBytes = SdkBytes.fromInputStream(sourceImageInputStream);

            // Detect faces in the source image
            DetectFacesRequest sourceDetectFacesRequest = DetectFacesRequest.builder()
                    .image(Image.builder().bytes(sourceImageBytes).build())
                    .build();

            DetectFacesResponse sourceDetectFacesResponse = rekognitionClient.detectFaces(sourceDetectFacesRequest);

            // Process the detected faces in the source image
            for (FaceDetail sourceFaceDetail : sourceDetectFacesResponse.faceDetails()) {
                BoundingBox sourceBoundingBox = sourceFaceDetail.boundingBox();

                // Specify the target image file with multiple faces
                String targetImageFilePath = "path/to/target/image.jpg"; // Replace with the path to the target image file

                // Load the target image from the file
                File targetImageFile = new File(targetImageFilePath);
                SdkBytes targetImageBytes;

                try (InputStream targetImageInputStream = new FileInputStream(targetImageFile)) {
                    targetImageBytes = SdkBytes.fromInputStream(targetImageInputStream);

                    // Create a request to compare each face in the target image
                    CompareFacesRequest compareFacesRequest = CompareFacesRequest.builder()
                            .sourceImage(Image.builder().bytes(sourceImageBytes).build())
                            .targetImage(Image.builder().bytes(targetImageBytes).build())
                            .similarityThreshold(80F) // Adjust the similarity threshold as needed
                            .build();

                    // Perform the face matching
                    CompareFacesResponse compareFacesResponse = rekognitionClient.compareFaces(compareFacesRequest);

                    // Process the comparison result
                    for (CompareFacesMatch match : compareFacesResponse.faceMatches()) {
                        System.out.println("Face match found:");
                        System.out.println("  Similarity: " + match.similarity());
                        System.out.println("  Source Face BoundingBox: " + sourceBoundingBox);
                        System.out.println("  Target Face BoundingBox: " + match.face().boundingBox());
                    }
                } catch (Exception e) {
                    System.err.println("Error comparing faces in the target image: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error detecting faces in the source image: " + e.getMessage());
        } finally {
            // Close the Rekognition client when done
            rekognitionClient.close();
        }
    }

}
