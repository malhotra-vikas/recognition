#!/bin/bash

# DynamoDB Table Details
TABLE_NAME="recognizedText.1"
S3_BUCKET="mediastore.primary.1"
REGION="us-east-2"
PARTITION_KEY_NAME="detectedText"
LOCAL_DIRECTORY="/Users/vikasmalhotra/Downloads/processedImages"

# SQS Queue URLs
QUEUE_URL_1="https://sqs.us-east-2.amazonaws.com/018701121298/ImagesUploaded1"
QUEUE_URL_2="https://sqs.us-east-2.amazonaws.com/018701121298/MetadataUpdated1"

# Function to get the number of messages in flight for a given queue
get_messages_in_flight() {
    local queue_url=$1
    echo $(aws sqs get-queue-attributes --queue-url "$queue_url" --attribute-names ApproximateNumberOfMessagesNotVisible --query 'Attributes.ApproximateNumberOfMessagesNotVisible' --output text)
}

# Initialize the number of messages in flight for both queues
IN_FLIGHT_1=1
IN_FLIGHT_2=1

# Loop until there are no messages in flight for both queues
while [ "$IN_FLIGHT_1" -ne 0 ] || [ "$IN_FLIGHT_2" -ne 0 ]; do
    # Check both queues
    IN_FLIGHT_1=$(get_messages_in_flight "$QUEUE_URL_1")
    IN_FLIGHT_2=$(get_messages_in_flight "$QUEUE_URL_2")

    # Output status for Queue 1
    if [ "$IN_FLIGHT_1" -eq 0 ]; then
        echo "No messages in flight for Queue 1."
    else
        echo "There are $IN_FLIGHT_1 messages in flight for Queue 1."
    fi

    # Output status for Queue 2
    if [ "$IN_FLIGHT_2" -eq 0 ]; then
        echo "No messages in flight for Queue 2."
    else
        echo "There are $IN_FLIGHT_2 messages in flight for Queue 2."
    fi

    # Wait for a short period before checking again
    sleep 5
done

echo "Both queues have no messages in flight."

# Getting ready to downloading images now
# Scan DynamoDB table and get items
ITEMS=$(aws dynamodb scan --table-name "$TABLE_NAME" --region "$REGION" --output json)

# Loop through each item
echo "$ITEMS" | jq -c '.Items[]' | while read -r ITEM; do
    # Extract detectedText and imageArtifacts
    DETECTED_TEXT=$(echo "$ITEM" | jq -r '.detectedText.S')
    IMAGE_ARTIFACTS=$(echo "$ITEM" | jq -r '.imageArtifacts.S')
    echo $DETECTED_TEXT

    # Create a directory for detectedText
    DIR_PATH="$LOCAL_DIRECTORY/$DETECTED_TEXT"
    echo "Creating directory $DIR_PATH"

    mkdir -p "$DIR_PATH"

    # Split the imageArtifacts by comma and download each file
    IFS=',' read -ra ADDR <<< "$IMAGE_ARTIFACTS"
    for URL in "${ADDR[@]}"; do
        FILE_NAME=$(basename "$URL")
        echo "Downloading S3 File $URL to $DIR_PATH/$FILE_NAME"

        S3URL="s3://$S3_BUCKET/$FILE_NAME"
        echo "Running command: s3 cp $S3URL"

        aws s3 cp "$S3URL" "$DIR_PATH/$FILE_NAME"
    done
done
