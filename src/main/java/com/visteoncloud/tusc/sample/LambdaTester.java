package com.visteoncloud.tusc.sample;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class LambdaTester {

	public static void main(String[] args) {
		
		// create handler
		LambdaHandler handler = new LambdaHandler();
		
		System.out.println("============================");
		
		// create request - array
		APIGatewayProxyRequestEvent request1 = new APIGatewayProxyRequestEvent();
		request1.setHttpMethod("post");
		request1.setBody("[{\"time\": 123, \"value\": 42.23}, {\"time\": 456, \"value\": 42.23}]");
		APIGatewayProxyResponseEvent response1 = handler.handleRequest(request1, new TestLambdaContext());
		System.out.println(response1.getStatusCode() + ": " + response1.getBody());
		
		System.out.println("============================");
		
		// create request - object
		APIGatewayProxyRequestEvent request2 = new APIGatewayProxyRequestEvent();
		request2.setHttpMethod("post");
		request2.setBody("{\"time\": 123, \"value\": 42.23}");
		APIGatewayProxyResponseEvent response2 = handler.handleRequest(request2, new TestLambdaContext());
		System.out.println(response2.getStatusCode() + ": " + response2.getBody());
		
		System.out.println("============================");
		
		// create request - missing time
		APIGatewayProxyRequestEvent request3 = new APIGatewayProxyRequestEvent();
		request3.setHttpMethod("post");
		request3.setBody("[{\"value\": 42.23}]");
		APIGatewayProxyResponseEvent response3 = handler.handleRequest(request3, new TestLambdaContext());
		System.out.println(response3.getStatusCode() + ": " + response3.getBody());
		
		System.out.println("============================");
		
		// create request - missing value
		APIGatewayProxyRequestEvent request4 = new APIGatewayProxyRequestEvent();
		request4.setHttpMethod("post");
		request4.setBody("[{\"time\": 123}]");
		APIGatewayProxyResponseEvent response4 = handler.handleRequest(request4, new TestLambdaContext());
		System.out.println(response4.getStatusCode() + ": " + response4.getBody());

	}

}