package com.visteoncloud.tusc.sample;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


import org.json.*;
import org.json.JSONObject;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>  
{
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
		
		// get logger
		LambdaLogger logger = context.getLogger();
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		JSONObject responseBody = new JSONObject();
		
		// get body
		try {
			JSONObject body = new JSONObject(input.getBody());
			String speed = body.getString("speed");
			
			// log body
			//logger.log("Speed: " + speed);
			logger.log("Request recieved.");
			logger.log("Method: " + input.getHttpMethod());
			logger.log("Path: " + input.getPath());
			logger.log("Raw body: " + input.getBody());
			
			String methodType = input.getHttpMethod();
			
			if(methodType.equals("GET")) { //calls GETRequest() if request.setHttpMethod("GET");
				logger.log("Calling GET method. Proceeding...");
				GETRequest();
			}
			else if(methodType.equals("POST")) { //calls POSTRequest() if request.setHttpMethod("POST");
				logger.log("Calling POST method. Proceeding...");
				POSTRequest();
			}
			else {
				logger.log("ERROR: Wrong method.");
			}
			// TODO: handle request data here	
			//responseBody.put("STATUS", "OK");
			
			// create and return response
			//response.setStatusCode(200);
			//response.setBody(responseBody.toString());

		} catch(IOException ex) {
			//ex.printStackTrace();
			responseBody.put("STATUS", 500);
	        responseBody.put("exception", ex);
	        //response.setBody(ex.getMessage());
		}
		//logger.log("Status code: " + response.getStatusCode());
		//logger.log("Response: " + response.toString());
		return response;
	}

	
	public static void GETRequest() throws IOException {
	    URL urlForGetRequest = new URL("https://fa705l8yui.execute-api.us-east-1.amazonaws.com/Prod/data");
	    String readLine = null;
	    HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
	    conection.setRequestMethod("GET");
	    conection.setRequestProperty("Speed", "a1bcdef"); // set userId its a sample here
	    int responseCode = conection.getResponseCode();
	    if (responseCode == HttpURLConnection.HTTP_OK) {
	        BufferedReader in = new BufferedReader(
	            new InputStreamReader(conection.getInputStream()));
	        StringBuffer response = new StringBuffer();
	        while ((readLine = in .readLine()) != null) {
	            response.append(readLine);
	        } in .close();
	        // print result
	        System.out.println("JSON String Result " + response.toString());
	        //GetAndPost.POSTRequest(response.toString());
	    } else {
	        System.out.println("ERROR: GET proccess.");
	        System.out.println("CODE: " + conection.getResponseCode());
	    }
	}
	public static void POSTRequest() throws IOException, MalformedURLException {
	    String POST_PARAMS = "[{ \"time\": 1562514130, "
	    		+ "\"value\": 42.3 }";
	   // System.out.println(POST_PARAMS);
	    URL dataURL = new URL("https://fa705l8yui.execute-api.us-east-1.amazonaws.com/Prod/data");
	    try {
		    HttpURLConnection postConnection = (HttpURLConnection) dataURL.openConnection();
		    postConnection.setRequestMethod("POST");
		    postConnection.setRequestProperty("userId", "a1bcdefgh");
		    postConnection.setRequestProperty("Content-Type", "application/json");
		    OutputStream os = postConnection.getOutputStream();
		    os.write(POST_PARAMS.getBytes());
		    os.flush();
		    os.close();
		    int responseCode = postConnection.getResponseCode();
		    System.out.println("POST Response Code :  " + responseCode);
		    System.out.println("POST Response Message : " + postConnection.getResponseMessage());
		    if (responseCode == HttpURLConnection.HTTP_CREATED) { //success
		        BufferedReader in = new BufferedReader(new InputStreamReader(
		            postConnection.getInputStream()));
		        String inputLine;
		        StringBuffer response = new StringBuffer();
		        while ((inputLine = in .readLine()) != null) {
		            response.append(inputLine);
		        } in .close();
		        // print result
		        System.out.println(response.toString());
		    } else if((responseCode == HttpURLConnection.HTTP_BAD_GATEWAY)) {
		    	System.out.println("ERROR IN POSTRequest: BAD GATEWAY.");
		    }
		    else if((responseCode == HttpURLConnection.HTTP_BAD_METHOD)) {
		    	System.out.println("ERROR IN POSTRequest: BAD METHOD.");
		    }
	    } catch(MalformedURLException e) {
	    	e.printStackTrace();
	    	System.out.println("ERROR: Bad URL for connection.");
	    } catch(IOException eIO) {
			//eIO.printStackTrace();
	    	System.out.println("ERROR: I/O problem.");
	    }
	}
}
