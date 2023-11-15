#!/bin/bash

# Variables
LOCAL_DIRECTORY="/Users/vikasmalhotra/Downloads/VideosbyRAF"
BUCKET_NAME="mediastore.primary.1"
DESTINATION="s3://$BUCKET_NAME/"
SCRIPTS_DIRECTORY="/Users/vikasmalhotra/Builderspace/recognition/scripts"

# Sync directory to S3
aws s3 sync "$LOCAL_DIRECTORY" "$DESTINATION" --exclude ".*"

# Check if sync command was successful
if [ $? -eq 0 ]; then
    echo "Directory sync successful."

    
    # Send all file names to the SQS Queue
    # SQS queue URL
    IMAGE_UPLOADED_QUEUE_URL="https://sqs.us-east-2.amazonaws.com/018701121298/ImagesUploaded1"

    # Listing files
    echo "Files in $LOCAL_DIRECTORY:"

    for FILE in "$LOCAL_DIRECTORY"/*
    do
        if [ -f "$FILE" ]; then
	    # Extract filename
	    FILENAME=$(basename "$FILE")

	    if [[ $FILENAME != .* ]]; then
                echo "$FILENAME" 

    	        # Message to send
	    	MESSAGE="$(basename "$FILE")"

	    	# Send the message
	    	aws sqs send-message --queue-url "$IMAGE_UPLOADED_QUEUE_URL" --message-body "$MESSAGE" --region us-east-2
	    fi
        fi
    done

else
    echo "Directory sync failed."
fi

# Start listening to the Queue Messages
bash "$SCRIPTS_DIRECTORY/download-images.sh"


