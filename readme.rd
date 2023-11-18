# Image Recognition
Link to the Documentation - https://docs.google.com/document/d/1v49cAttWX4FZx_Br7cWOfOVxOenL9IXUdSncV3AbRC0/edit


## Description
Briefly describe what the Image Recognition project does, its features, and its intended use cases.

## Installation (One time only)

This needs to be done once for each new computer that we are adding into the system

0. Install AWS CLI from https://aws.amazon.com/cli/
1. Open PowerShell as Administrator:
	A. Search for PowerShell in the Start menu.
	B. Right-click on PowerShell and select "Run as administrator".
2. Configure AWS CLI. You will need AWS Access Key and Secret for this
	A. On the terminal command line, run the "aws configure" command. 
	B. Enter AWS Access Key ID
	C. Enter AWS Secret Access Key
	D. Enter Default region name as "us-east-2"
	E. Enter Default output format as "json"
3. Test AWS CLI by running command "aws --version". This should return the current AWS CLI Version
4. Create 3 folders on Desktop. 
	A. drop-fotos: This folder will be used to drop fotos that needs to be processed. In the next version, this folder will be able to also hold fotos folders by events
	B. processed-fotos: This folder will be used to download all processed fotos after recognition. In the next version, this folder will be able to also hold fotos folders by events
	C. scripts: This folder will have 2 scripts. "upload-images-to-bucket",  "download-images"
5. Save the scripts from Google Drive (https://drive.google.com/drive/folders/1m7fC5A4iLBtR5i8VyXWedkwKKBZQe4Vv) to script folder 
6. Grant the scripts execution access
7. Open the upload-images-to-bucket file in the "scripts" folder and update the directories path on the file for LocalDirectory and ScriptDirectory
8. Open the download-images file in the "scripts" folder and update the directories path on the file for LocalDirectory
9. On the Scripts folder, right click the two files one by one and check Security -> Unblock
10.Open PowerShell as Administrator:
	A. Search for PowerShell in the Start menu.
	B. Right-click on PowerShell and select "Run as administrator".
11. Go to scripts folder on the PowerShell Window
12.Type "Set-ExecutionPolicy RemoteSigned". Press A when prompted

## Running
1. Copy all images that needs to be recognized into the "drop-photos" folder. Make sure there are only images on this folder AND that there are no sub-folders
2. Open PowerShell as Administrator:
	A. Search for PowerShell in the Start menu.
	B. Right-click on PowerShell and select "Run as administrator".
3. Type .\upload-images-to-bucket.ps1
4. Files will be processed and down-loaded in the "processed-fotos" folder



To use download script, you will need jq installed on your system, which is a lightweight and flexible command-line JSON processor. Here is how you can install jq if it's not already present:

On Ubuntu/Debian: sudo apt-get install jq
On CentOS/RedHat: sudo yum install jq
On macOS: brew install jq

```bash
# Example command to clone and install dependencies
git clone https://github.com/malhotra-vikas/recognition.git
cd recognition
# add other installation steps