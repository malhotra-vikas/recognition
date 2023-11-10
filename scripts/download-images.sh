#!/bin/bash

# Set your AWS credentials
AWS_DEFAULT_REGION="us-east-2"  # Change to your desired region

# Specify the S3 bucket and object (file) you want to download
S3_BUCKET="mediastore.1"

# Path to the CSV file containing the directory names and JSON with S3 details
CSV_FILE="/Users/vikasmalhotra/Downloads/processedImages/results.csv"

# Base directory where subdirectories will be created
BASE_DIRECTORY="/Users/vikasmalhotra/Downloads/processedImages"

# Read the CSV file line by line, skipping the header
tail -n +2 "$CSV_FILE" | while IFS=, read -r detectedText imageArtifacts
do
    # Remove extra quotes from imageArtifacts and split the string into URLs
    urls=$(echo $imageArtifacts | sed -e 's/^"//' -e 's/"$//' -e 's/, /,/g')

    # Process each URL
    IFS=',' read -ra ADDR <<< "$urls"
    for url in "${ADDR[@]}"; do
        echo "Processing URL: $url for detected text: $detectedText"
        # Add your URL processing logic here (e.g., download images)
    done
done

