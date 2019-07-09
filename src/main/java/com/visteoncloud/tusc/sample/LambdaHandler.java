package com.visteoncloud.tusc.sample;

import java.math.BigInteger;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>  {
	
	static DBClient dbClient = null;

	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
		
		// get logger
		LambdaLogger logger = context.getLogger();
		
		// create DB client
		if (dbClient == null) {
			dbClient = new DBClient();
		}
		
		logger.log("Received request with method :" + input.getHttpMethod());
		logger.log(input.getBody());
		
		// handle request
		APIGatewayProxyResponseEvent response;
		
		String method = input.getHttpMethod();
		if (method.equalsIgnoreCase("get")) {
			
			response = handleGet();
			
		} else if (method.equalsIgnoreCase("post")) {
			
			response = handlePost(input.getBody());
			
		} else {
			response = new APIGatewayProxyResponseEvent();
			response.setStatusCode(400);
			JSONObject responseBody = new JSONObject();
			responseBody.put("status", "error");
			responseBody.put("errorMessage", "Unsupported method : " + method);
			response.setBody(responseBody.toString());
		}
		
		return response;
	}
	
	private APIGatewayProxyResponseEvent handlePost(String body) {
		
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		JSONObject responseBody = new JSONObject();
		
		try {
			
			JSONArray requestBody = new JSONArray(body);
			HashMap<BigInteger, Float> dbData = new HashMap<BigInteger, Float>();
			
			// build data that can be inserted into DB
			for (int i = 0; i < requestBody.length(); i++) {
				JSONObject obj = requestBody.getJSONObject(i);
				BigInteger time = obj.getBigInteger("time");
				Float value = obj.getFloat("value");
				dbData.put(time, value);
			}
			
			// insert into DB
			dbClient.createItems("Demo user", dbData);
			
			responseBody.put("status", "ok");
			responseBody.put("data", dbData);
			
			response.setStatusCode(200);
			response.setBody(responseBody.toString(2));
			
		} catch (Exception e) {
			
			responseBody.put("status", "error");
			responseBody.put("errorMessage", e.toString());
			
			response.setStatusCode(400);
			response.setBody(responseBody.toString());
		}
		
		return response;
	}
	
	private APIGatewayProxyResponseEvent handleGet() {
		
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		JSONObject responseBody = new JSONObject();
		
		// build body
		responseBody.put("status", "error");
		responseBody.put("errorMessage", "Not implemented");
		
		// build response
		response.setStatusCode(501);
		response.setBody(responseBody.toString());
		
		return response;
	}

}