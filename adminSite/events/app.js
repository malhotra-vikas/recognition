// Create a DynamoDB instance
const dynamodb = new AWS.DynamoDB.DocumentClient();

// Define the DynamoDB table name
const tableName = "events"; // Replace with your DynamoDB table name

// Define the attribute name for event name
const eventNameAttribute = "event-name";

// Add an event listener for the "DOMContentLoaded" event
document.addEventListener("DOMContentLoaded", function() {
	// Call the fetchAndDisplayEvents function when the page loads
	fetchAndDisplayEvents();

	// Add an event listener for the form submission
	document.getElementById("eventForm").addEventListener("submit", handleFormSubmission);
});


// Function to fetch and display events
function fetchAndDisplayEvents() {

	// Define the parameters for scanning the DynamoDB table
	const params = {
		TableName: tableName,
	};

	// Use the DynamoDB scan operation to retrieve all items from the table
	dynamodb.scan(params, (error, data) => {
		if (error) {
			console.error("Error fetching events from DynamoDB:", error);
		} else {
			// Clear existing eventList content
			const eventList = document.getElementById("eventList");
			eventList.innerHTML = "";

			// Loop through the retrieved items and add them to the eventList
			data.Items.forEach((item) => {
				const eventName = item[eventNameAttribute]; // Assuming you have an "eventName" attribute in your DynamoDB table
				const eventRow = document.createElement("tr");
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
				
				eventList.appendChild(eventRow);
				
			});
		}
	});
}

// Function to handle form submission
function handleFormSubmission(event) {
	event.preventDefault();



	// Get the event name from the input field
	const eventName = document.getElementById("eventName").value;

	// Create an object to store event data
	const eventData = {
		[eventNameAttribute]: eventName, // Use the attribute name for event name
	};

	// Log the data to the console
	console.log("New Event Data:", eventData);

	// Add the new event to the DynamoDB table
	const params = {
		TableName: tableName,
		Item: eventData,
	};

	// Use the DynamoDB put operation to add the new event
	dynamodb.put(params, (error) => {
		if (error) {
			console.error("Error adding event to DynamoDB:", error);
		} else {
			// Call fetchAndDisplayEvents to refresh the event list
			fetchAndDisplayEvents();
		}
	});

	// Clear the input field
	document.getElementById("eventName").value = "";
}