#!/bin/bash
BUCKET_ID=$(dd if=/dev/random bs=8 count=1 2>/dev/null | od -An -tx1 | tr -d ' \t\n')

# Get today's date in YYYY-MM-DD format
TODAYS_DATE=$(date +"%Y-%m-%d")

BUCKET_NAME=event-$BUCKET_ID
echo $BUCKET_NAME > bucket-name.txt
aws s3api put-object --bucket "${BUCKET_NAME}" --key "${TODAYS_DATE}/"

