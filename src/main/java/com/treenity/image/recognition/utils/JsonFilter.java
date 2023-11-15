package com.treenity.image.recognition.utils;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonFilter {

	public JsonFilter() {
		super();
	}

	public String filterJsonByConfidence(int confidence, String jsonData) {

		JSONArray jsonArray = new JSONArray(jsonData);

		// Create a new JSONArray to hold the filtered entries
		JSONArray filteredArray = new JSONArray();

		// Iterate through the original array
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject entry = (JSONObject) jsonArray.get(i);

			//System.out.println("JSON ARRAY Entry : Before Filtering : + " + entry);

			// Check the Type
			String type = (String) entry.get("type");
			
			// Check if detected text is a number
			String detectedText = (String) entry.get("detectedText");
			
			boolean detectedTextIsNumber = false;
			if (NumberUtillity.isNumeric(detectedText)) {
				detectedTextIsNumber = true;
			}


			// Check the confidence value
			BigDecimal bigConfidence = (BigDecimal) entry.get("confidence");

			double entryConfidence = bigConfidence.doubleValue();

			if (detectedTextIsNumber && entryConfidence > confidence && type.equals("WORD")) {
				// Add entry to the filtered array if confidence is greater than 90% and the detected Text is a number
				filteredArray.put(entry);
			}
		}

		//System.out.println("FILTERED JSON ARRAY  : After Filtering : + " + filteredArray.toString(2));

		return filteredArray.toString(2);

	}

	public void extractDetectedText(String jsonData) {

		JSONArray jsonArray = new JSONArray(jsonData);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String detectedText = jsonObject.getString("detectedText");
			System.out.println(detectedText);
		}
	}

}
