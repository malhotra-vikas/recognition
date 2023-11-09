#!/bin/bash

# Set your AWS credentials
AWS_DEFAULT_REGION="us-east-2"  # Change to your desired region

# Specify the S3 bucket and object (file) you want to download
S3_BUCKET="mediastore.1"

# Path to the CSV file containing the directory names and JSON with S3 details
CSV_FILE="/Users/vikasmalhotra/Downloads/processedImages/results.csv"

# Base directory where subdirectories will be created
BASE_DIRECTORY="/Users/vikasmalhotra/Downloads/processedImages"

# Read the CSV file line by line
while IFS=, read -r column1 json_column
do
  # Remove leading and trailing quotes and replace escaped double quotes
  json_string=$(echo "$json_column" | sed -e 's/^"//' -e 's/"$//' -e 's/\\"/"/g')

  # Extract values from the JSON string using jq
  # Assuming the JSON is an array of objects with key "S"
  for row in $(echo "${json_string}" | jq -c '.[]'); do
    key=$(echo $row | jq -r '.S')

    # Determine if the key is a filename or a URL
    if [[ $key == http* ]]; then
      echo "URL: $key"
    else
      echo "File: $key"
    fi
  done

done < "$CSV_FILE"
