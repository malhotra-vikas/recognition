#!/bin/bash

# Variables
LOCAL_DIRECTORY="/Users/vikasmalhotra/Downloads/VideosbyRAF"
BUCKET_NAME="mediastore.1"
DESTINATION="s3://$BUCKET_NAME/"

# Sync directory to S3
aws s3 sync "$LOCAL_DIRECTORY" "$DESTINATION"

# Check if sync command was successful
if [ $? -eq 0 ]; then
    echo "Directory sync successful."
else
    echo "Directory sync failed."
fi

