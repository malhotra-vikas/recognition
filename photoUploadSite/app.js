var s3 = new AWS.S3();
var sqs = new AWS.SQS();
// Create a DynamoDB instance
var dynamodb = new AWS.DynamoDB.DocumentClient();

// Add an event listener for the "DOMContentLoaded" event
document.addEventListener("DOMContentLoaded", function() {

	// Call the fetchAndDisplayEvents function when the page loads
	fetchEventNames();
	fetchPhotographersNames();
	
	document.getElementById("uploadForm").addEventListener("submit", handleFormSubmission);

	// Add an event listener for the form submission
	//document.getElementById("eventForm").addEventListener("submit", handleFormSubmission);
});

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

// Function to fetch event names from DynamoDB
function fetchPhotographersNames() {
	// Define the DynamoDB table name
	const tableName = "Photographers"; // Replace with your DynamoDB table name
	
	// Define the attribute name for event name
	const key = "name";
	
	var params = {
		TableName: tableName // Replace with your DynamoDB table name
	};

	dynamodb.scan(params, function(err, data) {
		if (err) {
			console.error("Error fetching photographers names from DynamoDB:", err);
			return;
		}

        // Assuming your DynamoDB table has an attribute 'event-name'
        var photographerNames = data.Items.map(function(item) {
            return item['name']; // Adjust based on your DynamoDB schema
        });
        
		// Populate the dropdown menu
		var photographerNameDropdown = document.getElementById("photographerName");
		photographerNames.forEach(function(photographerName) {
			var option = document.createElement("option");
			option.value = photographerName;
			option.text = photographerName;
			photographerNameDropdown.appendChild(option);
		});
	});
}

