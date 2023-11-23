// Create a DynamoDB instance
const dynamodb = new AWS.DynamoDB.DocumentClient();

// Define the DynamoDB table name
const tableName = "recognizedText.1"; // Replace with your DynamoDB table name

// Define the attribute name for event name
const binAttribute = "detectedText";
const fotosAttribute = "imageArtifacts";


// Add an event listener for the "DOMContentLoaded" event
document.addEventListener("DOMContentLoaded", function() {
	
	// Add an event listener for the form submission
	document.getElementById("binForm").addEventListener("submit", handleFormSubmission);
});


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
			    

			
			    // Perform any other processing you need with the values
			  });
			} else {
			  console.log("No items found with the specified detectedText value.");
			}


			/*const eventRow = document.createElement("tr");
			const eventNameCell = document.createElement("td");
				const eventSinglePhotoPriceCell = document.createElement("td");
				const eventThreePhotoPriceCell = document.createElement("td");
				const eventPackPhotoPriceCell = document.createElement("td");

				
				eventNameCell.textContent = eventName;
				eventSinglePhotoPriceCell.textContent = "$10";
				eventThreePhotoPriceCell.textContent = "$25";
				eventPackPhotoPriceCell.textContent = "$45";
				
				eventRow.appendChild(eventNameCell);
				eventRow.appendChild(eventSinglePhotoPriceCell);
				eventRow.appendChild(eventThreePhotoPriceCell);
				eventRow.appendChild(eventPackPhotoPriceCell);
				
				eventList.appendChild(eventRow);*/
		}
	});
}

// Function to handle form submission
function handleFormSubmission(event) {
	event.preventDefault();

	// Get the event name from the input field
	const binName = document.getElementById("binName").value;
	console.log("Start searching for bin - " + binName);

	fetchAndDisplayFotos(binName);


	// Clear the input field
	document.getElementById("binName").value = "";
}