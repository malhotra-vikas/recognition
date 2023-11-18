# Check if the script is running as administrator
if (-Not ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
    # Restart the script as an administrator
    Start-Process powershell.exe -ArgumentList " -NoProfile -ExecutionPolicy Bypass -File `"$PSCommandPath`"" -Verb RunAs
    exit
}
$localDirectory = "C:\Users\malho_5edpim1\OneDrive\Desktop\processed-fotos"

# DynamoDB Table Details
$tableName = "recognizedText.1"
$s3Bucket = "mediastore.primary.1"
$region = "us-east-2"
$partitionKeyName = "detectedText"

# SQS Queue URLs
$queueUrl1 = "https://sqs.us-east-2.amazonaws.com/018701121298/ImagesUploaded1"
$queueUrl2 = "https://sqs.us-east-2.amazonaws.com/018701121298/MetadataUpdated1"

function Get-MessagesInFlight {
    param ($queueUrl)
    return (aws sqs get-queue-attributes --queue-url $queueUrl --attribute-names ApproximateNumberOfMessagesNotVisible --query 'Attributes.ApproximateNumberOfMessagesNotVisible' --output text).Trim()
}

# Initialize the number of messages in flight for both queues
$inFlight1 = 1
$inFlight2 = 1

# Loop until there are no messages in flight for both queues
while ($inFlight1 -ne 0 -or $inFlight2 -ne 0) {
    # Check both queues
    $inFlight1 = Get-MessagesInFlight -queueUrl $queueUrl1
    $inFlight2 = Get-MessagesInFlight -queueUrl $queueUrl2

    # Output status for Queue 1
    if ($inFlight1 -eq 0) {
        Write-Host "No messages in flight for Queue 1."
    } else {
        Write-Host "There are $inFlight1 messages in flight for Queue 1."
    }

    # Output status for Queue 2
    if ($inFlight2 -eq 0) {
        Write-Host "No messages in flight for Queue 2."
    } else {
        Write-Host "There are $inFlight2 messages in flight for Queue 2."
    }

    # Wait for a short period before checking again
    Start-Sleep -Seconds 5
}

Write-Host "Both queues have no messages in flight."

# Getting ready to download images now
# Scan DynamoDB table and get items
$items = aws dynamodb scan --table-name $tableName --region $region --output json | ConvertFrom-Json
Write-Host "Scanned Items"

# Create Directories for Detected Text
foreach ($item in $items.Items) {
    $detectedText = $item.detectedText.S
    $imageArtifacts = $item.imageArtifacts.S
    Write-Host "Detected Text $detectedText"
    Write-Host "Image Artifacts $imageArtifacts"

    # Create a directory for detectedText
    $dirPath = Join-Path -Path $localDirectory -ChildPath $detectedText
    Write-Host "Creating directory $dirPath"
    New-Item -Path $dirPath -ItemType Directory -Force

    # Split the imageArtifacts by comma and download each file
    $artifactUrls = $imageArtifacts -split ","
    foreach ($url in $artifactUrls) {
        $fileName = [System.IO.Path]::GetFileName($url)
        Write-Host "Downloading S3 File $url to $($dirPath)\$fileName"

        $s3Url = "s3://$s3Bucket/$fileName"
        aws s3 cp $s3Url "$($dirPath)\$fileName"
    }
}
