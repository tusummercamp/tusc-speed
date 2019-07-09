package com.visteoncloud.tusc.sample;

import java.util.List;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;


public class DBClient {
	
	AmazonDynamoDB client;
    DynamoDB dynamoDB;
	String tableName;

	public DBClient() {
		System.out.println("Creating DynamoDB connection");
		client = AmazonDynamoDBClientBuilder.defaultClient();
		dynamoDB = new DynamoDB(client);
		tableName = System.getenv("DYNAMODB_TABLE");
		System.out.println("Connected to DB");
	}
	
	public void createItems(String user, HashMap<BigInteger, Float> data) {
		
		// create ArrayList<Item> to hold data we are going to insert in the DB
		ArrayList<Item> items = new ArrayList<Item>();
		
		// iterate over the data
		Iterator<Entry<BigInteger, Float>> it = data.entrySet().iterator();
		while (it.hasNext()) {
			Entry<BigInteger, Float> entry = it.next();
			Item item = new Item();
			item.withPrimaryKey("User", user, "Time", entry.getKey());
			item.withNumber("Value", entry.getValue());
			items.add(item);
		}
		
		// create write items
		TableWriteItems writeItems = new TableWriteItems(tableName).withItemsToPut(items);
		
		// do the write	
		BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(writeItems);
		do {
			
			Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
			if (outcome.getUnprocessedItems().size() == 0) {
				System.out.println("All items have been written");
			} else {
				System.out.println("Writing unprocessed items");
				outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
			}
			
		} while (outcome.getUnprocessedItems().size() > 0);
		
	}
	
}