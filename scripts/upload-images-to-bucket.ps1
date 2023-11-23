# Check if the script is running as administrator
if (-Not ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
    # Restart the script as an administrator
    Start-Process powershell.exe -ArgumentList " -NoProfile -ExecutionPolicy Bypass -File `"$PSCommandPath`"" -Verb RunAs
    exit
}

# Variables
$localDirectory = "/Users/vikasmalhotra/Downloads/drop-fotos"
$scriptsDirectory = "C:\Users\malho_5edpim1\OneDrive\Desktop\scripts"

$bucketName = "mediastore.primary.1"
$eventName = "De Marfta"
$photographerName = "De Jose"
$destination = "s3://$bucketName/$eventName/$photographerName"
$imageUploadedQueueUrl = "https://sqs.us-east-2.amazonaws.com/018701121298/ImagesUploaded1"

# Sync directory to S3
$syncResult = aws s3 sync $localDirectory $destination --exclude ".*"

# Check if sync command was successful
if ($LASTEXITCODE -eq 0) {
    Write-Host "Directory sync successful."

    # Send all file names to the SQS Queue
    Write-Host "Files in ${localDirectory}"

    Get-ChildItem $localDirectory -File | ForEach-Object {
        $fileName = $_.Name

        # Skip hidden files
        if (-not $fileName.StartsWith('.')) {
            Write-Host $fileName

            # Message to send
            $message = $fileName
            $message = $eventName + ":" + $photographerName + ":" + $fileName
	    
	    Write-Host $message
            # Send the message
            aws sqs send-message --queue-url $imageUploadedQueueUrl --message-body $message --region us-east-2
        }
    }
} else {
    Write-Host "Directory sync failed."
}

# Start listening to the Queue Messages
# & "$scriptsDirectory\download-images.ps1"
