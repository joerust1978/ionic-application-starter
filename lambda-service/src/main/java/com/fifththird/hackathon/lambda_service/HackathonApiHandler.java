package com.fifththird.hackathon.lambda_service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.TimeZone;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.sms.SmsSubmissionResponse;
import com.nexmo.client.sms.SmsSubmissionResponseMessage;
import com.nexmo.client.sms.messages.TextMessage;

public class HackathonApiHandler implements RequestStreamHandler {

	private LambdaLogger logger;

	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		TimeZone.setDefault(TimeZone.getTimeZone("EST"));
		JSONObject jsonResponse = new JSONObject();

		logger = context.getLogger();
		try {
			InboundRequest request = getInboundRequest(input);

			switch (request.getPath()) {
			case "/sms-alert":
				logger.log(request.getBody().toJSONString());
				if (sendTextMessage(request.getBody().get("phoneNumber").toString(),
						request.getBody().get("message").toString())) {
					jsonResponse = createSuccessResponse("SUCCESS");
				} else {
					jsonResponse = createErrorResponse("FAILURE");
				}
				break;
			default:
				break;
			}
		} catch (ParseException ex) {
			jsonResponse = createErrorResponse(ex.getMessage());
		}
		writeResponse(jsonResponse, output);
	}

	public static final String FROM_NUMBER = System.getenv("FROM_NUMBER");
	public static final String NEXMO_API_SECRET = System.getenv("API_SECRET");
	public static final String NEXMO_API_KEY = System.getenv("API_KEY");

	public static final JSONObject HEADERS = new JSONObject();

	static {
		HEADERS.put("Content-Type", "application/json");
		HEADERS.put("Access-Control-Allow-Origin", "*");
	}

	private JSONParser parser = new JSONParser();

	protected InboundRequest getInboundRequest(InputStream inputStream) throws IOException, ParseException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		JSONObject event = (JSONObject) parser.parse(reader);
		String reqBody = event.get("body").toString();

		return new InboundRequest(event.get("path").toString(), (JSONObject) parser.parse(reqBody));
	}

	protected void writeResponse(JSONObject jsonResponse, OutputStream outputStream) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
		writer.write(jsonResponse.toJSONString());
		writer.close();

	}

	protected JSONObject createErrorResponse(String message) {
		return createErrorResponse(message, 500);
	}

	protected JSONObject createErrorResponse(String message, int statusCode) {
		return createResponse("{ \"error\": \"" + message + "\"}", statusCode);
	}

	protected JSONObject createEmptySuccessResponse() {
		return createResponse("{ \"success\": true }", 200);
	}

	protected JSONObject createSuccessResponse(String body) {
		return createResponse(body, 200);
	}

	protected JSONObject createSuccessResponse(Map<String, Object> body) {
		return createResponse(mapToJsonString(body), 200);
	}

	protected JSONObject createResponse(String body, int statusCode) {
		JSONObject response = new JSONObject();
		response.put("statusCode", statusCode);
		response.put("headers", HEADERS);
		response.put("body", body);
		return response;
	}

	private String mapToJsonString(Map<String, Object> input) {
		return new JSONObject(input).toJSONString();
	}

	private boolean sendTextMessage(String to, String messageText) {
		NexmoClient client = new NexmoClient.Builder().apiKey(NEXMO_API_KEY).apiSecret(NEXMO_API_SECRET).build();

		TextMessage message = new TextMessage(FROM_NUMBER, to, messageText);

		try {
			SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

			for (SmsSubmissionResponseMessage responseMessage : response.getMessages()) {
				logger.log(responseMessage.toString());
			}
		} catch (Exception e) {
			logger.log(e.getMessage());
			return false;
		}

		return true;

	}

}
