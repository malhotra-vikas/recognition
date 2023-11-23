// Create a DynamoDB instance
const dynamodb = new AWS.DynamoDB.DocumentClient();

// Define the DynamoDB table name
const tableName = "Photographers"; // Replace with your DynamoDB table name

// Define the attribute name for event name
const photographerNameAttribute = "name";

// Add an event listener for the "DOMContentLoaded" event
document.addEventListener("DOMContentLoaded", function() {
	// Call the fetchAndDisplayEvents function when the page loads
	fetchAndDisplayPhotographers();

	// Add an event listener for the form submission
	document.getElementById("photographerForm").addEventListener("submit", handleFormSubmission);
});


// Function to fetch and display events
function fetchAndDisplayPhotographers() {

	// Define the parameters for scanning the DynamoDB table
	const params = {
		TableName: tableName,
	};

	// Use the DynamoDB scan operation to retrieve all items from the table
	dynamodb.scan(params, (error, data) => {
		if (error) {
			console.error("Error fetching photographers from DynamoDB:", error);
		} else {
			// Clear existing content
			const photographerList = document.getElementById("photographerList");
			photographerList.innerHTML = "";

			// Loop through the retrieved items and add them to the list
			data.Items.forEach((item) => {
				const photographerName = item[photographerNameAttribute]; // Assuming you have an "eventName" attribute in your DynamoDB table
				const photographerRow = document.createElement("tr");
				const photographerNameCell = document.createElement("td");
				const photographerRateCell = document.createElement("td");

				
				photographerNameCell.textContent = photographerName;
				photographerRateCell.textContent = "$10";

				
				photographerRow.appendChild(photographerNameCell);
				photographerRow.appendChild(photographerRateCell);
				
				photographerList.appendChild(photographerRow);
				
			});
		}
	});
}

// Function to handle form submission
function handleFormSubmission(event) {
	event.preventDefault();



	// Get the event name from the input field
	const photographerName = document.getElementById("photographerName").value;

	// Create an object to store event data
	const photographerData = {
		[photographerNameAttribute]: photographerName, // Use the attribute name for event name
	};

	// Log the data to the console
	console.log("New Event Data:", photographerData);

	// Add the new event to the DynamoDB table
	const params = {
		TableName: tableName,
		Item: photographerData,
	};

	// Use the DynamoDB put operation to add the new event
	dynamodb.put(params, (error) => {
		if (error) {
			console.error("Error adding event to DynamoDB:", error);
		} else {
			// Call fetchAndDisplayEvents to refresh the event list
			fetchAndDisplayPhotographers();
		}
	});

	// Clear the input field
	document.getElementById("photographerName").value = "";
}