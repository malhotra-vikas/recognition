// Create a DynamoDB instance
const dynamodb = new AWS.DynamoDB.DocumentClient();

// Define the DynamoDB table name
const tableName = "recognizedTextEvent"; // Replace with your DynamoDB table name

// Define the attribute name for event name
const binAttribute = "detectedText";
const fotosAttribute = "imageArtifacts";


// Add an event listener for the "DOMContentLoaded" event
document.addEventListener("DOMContentLoaded", function() {

// Call the fetchAndDisplayEvents function when the page loads
	//fetchEventNames();
	
	// Add an event listener for the form submission
	document.getElementById("binForm").addEventListener("submit", handleFormSubmission);
	//document.getElementById('downloadBinImagesButton').addEventListener('click', downloadBinImages);
	document.getElementById('downloadAllButton').addEventListener('click', downloadAllImagesForBin);
});

        // Function to show the spinner
        function showSpinner() {
            document.getElementById("spinner-container").style.display = "flex";
        }

        // Function to hide the spinner
        function hideSpinner() {
            document.getElementById("spinner-container").style.display = "none";
        }

        function updateProgressBarAndCount(completedDownloads, imageFolderLength) {
            // Calculate the progress percentage
            var percent = (completedDownloads / imageFolderLength) * 100;

            // Update the progress bar width
            document.getElementById("progress-bar").style.width = percent + "%";

            // Update the count text
            document.getElementById("progress-text").textContent = completedDownloads + " / " + imageFolderLength;
        }
        
// Function to fetch event names from DynamoDB
function fetchEventNames() {
	// Define the DynamoDB table name
	const tableName = "events"; // Replace with your DynamoDB table name
	
	// Define the attribute name for event name
	const eventKey = "event-name";

	var params = {
		TableName: tableName // Replace with your DynamoDB table name
	};

	dynamodb.scan(params, function(err, data) {
		if (err) {
			console.error("Error fetching event names from DynamoDB:", err);
			return;
		}

        // Assuming your DynamoDB table has an attribute 'event-name'
        var eventNames = data.Items.map(function(item) {
            return item['event-name']; // Adjust based on your DynamoDB schema
        });
        
		// Populate the dropdown menu
		var eventNameDropdown = document.getElementById("eventName");
		eventNames.forEach(function(eventName) {
			var option = document.createElement("option");
			option.value = eventName;
			option.text = eventName;
			eventNameDropdown.appendChild(option);
		});
	});
}

function isValidImageMimeType(mimeType) {
  const validImageMimeTypes = ['image/jpeg']; // Add more as needed
  return validImageMimeTypes.includes(mimeType.toLowerCase());
}

function compareArrays(array1, array2) {
  if (array1.length !== array2.length) {
    return false;
  }

  for (let i = 0; i < array1.length; i++) {
    if (array1[i] !== array2[i]) {
      return false;
    }
  }

  return true;
}

function isValidImageHeader(arrayBuffer) {
  // Define image format headers (add more as needed)
  const jpegHeader = [0xFF, 0xD8]; // JPEG


  const headerBytes = new Uint8Array(arrayBuffer.slice(0, 4)); // Read the first 4 bytes

  // Compare the header bytes to known image format headers
  if (
    compareArrays(headerBytes, jpegHeader)
  ) {
    return true;
  }

  return false;
}

function compareArrays(array1, array2) {
  if (array1.length !== array2.length) {
    return false;
  }

  for (let i = 0; i < array1.length; i++) {
    if (array1[i] !== array2[i]) {
      return false;
    }
  }

  return true;
}

function removeDuplicateImages(imageUrls) {
  // Use a Set to store unique image URLs
  const uniqueUrls = new Set(imageUrls);

  // Convert the Set back to an array to maintain the order
  const uniqueImageUrls = [...uniqueUrls];

  return uniqueImageUrls;
}


function downloadAllImagesForBin(imageUrls, bin) {
	showSpinner();


    // Select all image elements within the photosContainer
    const images = document.querySelectorAll('#photosContainer a');
    
	console.log("Attempting to doaload all images for the bin " + images.length);

	const uniqueImages = removeDuplicateImages(images);

	console.log("Attempting to doaload all unique images for the bin " + uniqueImages.length);

	
    // Create an array to store the fetch promises
    const fetchPromises = [];

    // Create a zip file to store the images
    const zip = new JSZip();

    // Iterate through the images and add them to the zip file
    uniqueImages.forEach((image, index) => {
        const imageUrl = image;
        console.log("Image URL to fetch is - " + imageUrl);
		// Split the URL by '/' to get the parts
		const parts = String(imageUrl).split('/');

		// The last part (after the last '/') should be the image name
		const imageName = parts[parts.length - 1];

		console.log("Image Name:", imageName);

        fetchPromises.push(
	        fetch(imageUrl)
	            .then(response => response.blob())
	            .then(blob => {
			      console.log("Downloading Image Name:", imageName);
				  zip.file(imageName, blob);
	            })
	        );
    });

    bin = document.getElementById("binName").value;

    // Wait for all fetch operations to complete
    Promise.all(fetchPromises)
        .then(() => {
        	// Generate and trigger the download of the zip file
		    zip.generateAsync({ type: 'blob' }).then(function(content) {
		        const downloadLink = document.createElement('a');
		        downloadLink.href = URL.createObjectURL(content);
		        downloadLink.download = bin + '.zip'; // Customize the zip file name if needed
		        downloadLink.click();
		        hideSpinner();
    		});
    	})
	    .catch(error => {
	            console.error("Error fetching images:", error);
	        });

	}


// Function to fetch and display events
function fetchAndDisplayFotos(binToSearch) {

	// Define the parameters for scanning the DynamoDB table
	const params = {
		TableName: tableName,
		KeyConditionExpression: "detectedText = :value",
		ExpressionAttributeValues: {
			":value": binToSearch,
		},
	};

	// Use the DynamoDB scan operation to retrieve all items from the table
	dynamodb.query(params, (error, data) => {
		if (error) {
			console.error("Error fetching events from DynamoDB:", error);
		} else {
			const photosContainer = document.getElementById("photosContainer");

			// Clear the existing content inside the container
			photosContainer.innerHTML = "";


			const searchResultItems = data.Items;
			console.log("Items with detectedText value :", searchResultItems);

			if (searchResultItems && searchResultItems.length > 0) {
				let index = 0;
				
				// Iterate through the retrieved items
				searchResultItems.forEach((item) => {
					// Access attributes by their attribute names
					const detectedText = item.detectedText; // Replace with your actual attribute name
					const imageArtifacts = item.imageArtifacts; // Replace with your actual attribute name

					// You can now work with the values of the attributes
					console.log("Detected Text:", detectedText);
					console.log("Image Artifacts:", imageArtifacts);

					// Split the imageArtifactsString into an array using ', ' as the delimiter
					const imageArtifactsArray = imageArtifacts.split(', ');
					// Create a new table for each set of URLs
					const table = document.createElement("table");
					table.className = "image-table"; // You can define CSS styles for this class

					// Iterate through the individual URLs in the array and create table rows
					imageArtifactsArray.forEach((url) => {
						const row = table.insertRow();
						const cell = row.insertCell();

						// Create an anchor element with the URL as the href attribute
						const link = document.createElement("a");
						link.href = url;
						link.textContent = url;

						cell.appendChild(link);
						// You can apply additional styling or functionality to the cells as needed
					});

					// Add the table to the container
					photosContainer.appendChild(table);

					// Add a separator (optional) between tables
					if (index < data.Items.length - 1) {
						const separator = document.createElement("hr");
						separator.className = "separator";
						container.appendChild(separator);
					}

					//displayPhotos(imageArtifactsArray, 200, 150);


					// Perform any other processing you need with the values
				});
			} else {
				console.log("No items found with the specified detectedText value.");
			}
		}
	});
}

// Function to trigger the download
function downloadBinImages(imageUrls) {
	imageUrls.forEach((imageUrl, index) => {
		// Create an anchor element
		const anchor = document.createElement('a');
		anchor.href = imageUrl;
		anchor.download = `image${index + 1}.jpg`;

		// Simulate a click on the anchor element to trigger download
		anchor.click();
	});
}

    function displayPhotos(photos, maxWidth, maxHeight) {
    // Clear the existing content in the photos container
    photosContainer.innerHTML = "";
    console.log("Display Photo " + maxHeight);


    photos.forEach(photo => {
        const img = document.createElement("img");
        const imgWidth = photo.width; // Width of the original image
        const imgHeight = photo.height; // Height of the original image

        // Calculate the new dimensions while maintaining aspect ratio
        let newWidth, newHeight;

        if (imgWidth > imgHeight) {
            newWidth = maxWidth;
            newHeight = (imgHeight / imgWidth) * maxWidth;
        } else {
            newHeight = maxHeight;
            newWidth = (imgWidth / imgHeight) * maxHeight;
        }

        // Set the new dimensions for the image
        img.width = newWidth;
        img.height = newHeight;

        // Set the src attribute to the URL of the photo
        img.src = `${photo}`;
        console.log("Showing Photo " + img);

        // Append the resized image to the photos container
        photosContainer.appendChild(img);
    });
    }
    
// Function to handle form submission
function handleFormSubmission(event) {
	event.preventDefault();

	// Get the event name from the input field
	const binName = document.getElementById("binName").value;
	console.log("Start searching for bin - " + binName);

	fetchAndDisplayFotos(binName);
}