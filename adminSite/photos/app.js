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
		Key: {
    		binAttribute: binToSearch, // Specify the primary key attribute and its value
  		},
	};

	// Use the DynamoDB scan operation to retrieve all items from the table
	dynamodb.get(params, (error, data) => {
		if (error) {
			console.error("Error fetching events from DynamoDB:", error);
		} else {
			// Clear existing eventList content
			const binList = document.getElementById("binList");
			binList.innerHTML = "";

			// Handle the retrieved event data here
			const binDataItem = data.Item; // The item with the specified event ID
			console.log("Retrieved Event Data:", binDataItem);
			
			const imageArtifacts = binDataItem[fotosAttribute]; // Assuming you have an "eventName" attribute in your DynamoDB table
			
			console.log("Retrieved for Bin:", binToSearch);
			console.log("Retrieved imageArtifacts:", imageArtifacts);

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