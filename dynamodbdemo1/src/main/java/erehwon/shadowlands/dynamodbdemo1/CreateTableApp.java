package erehwon.shadowlands.dynamodbdemo1;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import erehwon.shadowlands.dynamodbdemo1.model.*;


public class CreateTableApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String DYNAMO_HOST = "http://localhost:8000";
		String DYNAMO_REGION = Regions.AP_SOUTHEAST_2.getName();
		String AWS_ACCESS_KEY_ID = "accesskey";
		String AWS_SECRET_ACCESS_KEY = "dummy";
		
	     AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
	             .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(DYNAMO_HOST, DYNAMO_REGION))
	             .build();

	         DynamoDB dynamoDB = new DynamoDB(client);
	         DynamoDBMapper dynamoDBMapper  = new DynamoDBMapper(client);

	         String tableName = "Movies";
	         deleteTable(dynamoDB, tableName);

	         try {
	             System.out.println("Attempting to create table; please wait...");
	             Table table = dynamoDB.createTable(tableName,
	                 Arrays.asList(new KeySchemaElement("year", KeyType.HASH), // Partition
	                                                                           // key
	                     new KeySchemaElement("title", KeyType.RANGE)), // Sort key
	                 Arrays.asList(new AttributeDefinition("year", ScalarAttributeType.N),
	                     new AttributeDefinition("title", ScalarAttributeType.S)),
	                 new ProvisionedThroughput(10L, 10L));
	             table.waitForActive();
	             System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());

	         }
	         catch (Exception e) {
	             System.err.println("Unable to create table: ");
	             System.err.println(e.getMessage());
	         }

         createRow(dynamoDB, tableName)   ;
         incrementCounter(dynamoDB, tableName);
         readRow(dynamoDB, tableName);
         updateRow(dynamoDB, tableName);
         incrementCounter(dynamoDB, tableName);
         readRow(dynamoDB, tableName);
         conditionalUpdate(dynamoDB, tableName);
         readRow(dynamoDB, tableName);
         DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                 .withPrimaryKey(new PrimaryKey("year", 2015, "title", "The Big New Movie")).withConditionExpression("info.rating <= :val")
                 .withValueMap(new ValueMap().withNumber(":val", 5.0));

         deleteItem(dynamoDB, tableName, deleteItemSpec);
         deleteItemSpec = new DeleteItemSpec()
                 .withPrimaryKey(new PrimaryKey("year", 2015, "title", "The Big New Movie"));         
         deleteItem(dynamoDB, tableName, deleteItemSpec);
         readRow(dynamoDB, tableName);
         String workingDir = System.getProperty("user.dir");
  	   	 System.out.println("Current working directory : " + workingDir);
  	     workingDir += File.separator + System.getProperty("sun.java.command") .substring(0, System.getProperty("sun.java.command").lastIndexOf(".")) .replace(".", File.separator);
  	   	 System.out.println("Current class directory : " + workingDir);
//         loadTable(dynamoDB, tableName);
         loadTable(dynamoDBMapper);

	}
	
    private static void createRow(DynamoDB dynamoDB, String tableName) {
        Table table = dynamoDB.getTable("Movies");

        int year = 2015;
        String title = "The Big New Movie";

        final Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put("plot", "Nothing happens at all.");
        infoMap.put("rating", 0);

        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table
                .putItem(new Item().withPrimaryKey("year", year, "title", title).withMap("info", infoMap));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

        }
        catch (Exception e) {
            System.err.println("Unable to add item: " + year + " " + title);
            System.err.println(e.getMessage());
        }
	
    }

    private static void createRow(DynamoDBMapper dynamoDBMapper, MovieRecord movieRec) {

        int year = movieRec.getYear().intValue();
        String title = movieRec.getTitle();


        try {
            System.out.println("Adding a new item...");

            dynamoDBMapper.save(movieRec);

            System.out.println("PutItem succeeded:\n" +  year + ", :" + title +":");

        }
        catch (Exception e) {
            System.err.println("Unable to add item: " + year + " " + title);
            System.err.println(e.getMessage());
        }

    }

    private static void readRow(DynamoDB dynamoDB, String tableName) {
        int year = 2015;
        String title = "The Big New Movie";

        Table table = dynamoDB.getTable("Movies");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("year", year, "title", title);

        try {
            System.out.println("Attempting to read the item...");
            Item outcome = table.getItem(spec);
            System.out.println("GetItem succeeded: " + outcome.toJSONPretty());
            Iterator iter = outcome.attributes().iterator();
//            while (iter.hasNext()) {
//               Object obj = iter.next();
//               System.out.println(obj.toString());
//            }
            

        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + year + " " + title);
            System.err.println(e.getMessage());
        }
   	
    }
    
    private static void updateRow(DynamoDB dynamoDB, String tableName) {
        Table table = dynamoDB.getTable("Movies");

        int year = 2015;
        String title = "The Big New Movie";

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("year", year, "title", title)
            .withUpdateExpression("set info.rating = :r, info.plot=:p, info.actors=:a")
            .withValueMap(new ValueMap().withNumber(":r", 5.5).withString(":p", "Everything happens all at once.")
                .withList(":a", Arrays.asList("Larry", "Moe", "Curly")))
            .withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            System.out.println("Updating the item...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println("Unable to update item: " + year + " " + title);
            System.err.println(e.getMessage());
        }
   	
    }

    private static void incrementCounter(DynamoDB dynamoDB, String tableName) {
        Table table = dynamoDB.getTable("Movies");

        int year = 2015;
        String title = "The Big New Movie";

        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("year", year, "title", title)
            .withUpdateExpression("set info.rating = info.rating + :val")
            .withValueMap(new ValueMap().withNumber(":val", 1)).withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            System.out.println("Incrementing an atomic counter...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println("Unable to update item: " + year + " " + title);
            System.err.println(e.getMessage());
        }
    	
    }

    private static void conditionalUpdate(DynamoDB dynamoDB, String tableName) {
        Table table = dynamoDB.getTable("Movies");

        int year = 2015;
        String title = "The Big New Movie";

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey(new PrimaryKey("year", year, "title", title)).withUpdateExpression("remove info.actors[0]")
                .withConditionExpression("size(info.actors) >= :num").withValueMap(new ValueMap().withNumber(":num", 3))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        // Conditional update (we expect this to fail)
        try {
            System.out.println("Attempting a conditional update...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println("Unable to update item: " + year + " " + title);
            System.err.println(e.getMessage());
        }

    }

    private static void deleteItem(DynamoDB dynamoDB, String tableName, DeleteItemSpec deleteItemSpec){
        Table table = dynamoDB.getTable("Movies");

        int year = 2015;
        String title = "The Big New Movie";


        // Conditional delete (we expect this to fail)

        try {
            System.out.println("Attempting a conditional delete...");
            table.deleteItem(deleteItemSpec);
            System.out.println("DeleteItem succeeded");
        }
        catch (Exception e) {
            System.err.println("Unable to delete item: " + year + " " + title);
            System.err.println(e.getMessage());
        }

    }

    private static void deleteTable(DynamoDB dynamoDB, String tableName) {
        Table table = dynamoDB.getTable(tableName);
        try {
        	System.out.println("Issuing DeleteTable request for " + tableName);
            table.delete();

            System.out.println("Waiting for " + tableName + " to be deleted...this may take a while...");
            table.waitForDelete();

        } catch (ResourceNotFoundException rnfe) {
            // Ignore - doesn't matter if it doesn't already exist
        } catch (Exception e) {
        	System.out.println("DeleteTable request failed for " + tableName + ": " + e.toString());
        }
    }

    private static void loadTable(DynamoDB dynamoDB, String tableName) {

        Table table = dynamoDB.getTable(tableName);

        JsonParser parser;
        JsonNode rootNode;
        try {
            String myCurrentDir = System.getProperty("user.dir") + File.separator + System.getProperty("sun.java.command") .substring(0, System.getProperty("sun.java.command").lastIndexOf(".")) .replace(".", File.separator);
            System.out.println(myCurrentDir);
            // these 2 work for Eclipse
//			parser = new JsonFactory().createParser(new File("target\\classes\\moviedata.json"));
//            parser = new JsonFactory().createParser(new File("src\\main\\resources\\moviedata.json"));
            // these 2 work for Intellij
//            parser = new JsonFactory().createParser(new File("D:\\Warehouse\\git_clones\\AWS_demos\\dynamodbdemo1\\src\\main\\resources\\moviedata.json"));
            parser = new JsonFactory().createParser(new File("D:\\Warehouse\\git_clones\\AWS_demos\\dynamodbdemo1\\target\\classes\\moviedata.json"));
            rootNode = new ObjectMapper().readTree(parser);
            Iterator<JsonNode> iter = rootNode.iterator();

            ObjectNode currentNode;
            int counter = 0;
            while (iter.hasNext()) {
                currentNode = (ObjectNode) iter.next();

                // extract the primary key
                int year = currentNode.path("year").asInt();
                String title = currentNode.path("title").asText();

                try {
                	counter++;
                    table.putItem(new Item().withPrimaryKey("year", year, "title", title).withJSON("info",
                            currentNode.path("info").toString()));
                    System.out.println("PutItem succeeded: " + counter + "[" + year + " " + title + "]");

                }
                catch (Exception e) {
                    System.err.println("Unable to add movie: " + year + " " + title);
                    System.err.println(e.getMessage());
                    break;
                }
            }
            parser.close();
            System.out.println("Loaded " + counter + " items");
        } catch (JsonParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }
    private static void loadTable(DynamoDBMapper dynamoDBMapper) {

        JsonParser parser;
        JsonNode rootNode;
        try {
            String myCurrentDir = System.getProperty("user.dir") + File.separator + System.getProperty("sun.java.command") .substring(0, System.getProperty("sun.java.command").lastIndexOf(".")) .replace(".", File.separator);
            System.out.println(myCurrentDir);
            // these 2 work for Eclipse
//			parser = new JsonFactory().createParser(new File("target\\classes\\moviedata.json"));
//            parser = new JsonFactory().createParser(new File("src\\main\\resources\\moviedata.json"));
            // these 2 work for Intellij
//            parser = new JsonFactory().createParser(new File("D:\\Warehouse\\git_clones\\AWS_demos\\dynamodbdemo1\\src\\main\\resources\\moviedata.json"));
            parser = new JsonFactory().createParser(new File("D:\\Warehouse\\git_clones\\AWS_demos\\dynamodbdemo1\\src\\main\\resources\\moviedata.json"));
            rootNode = new ObjectMapper().readTree(parser);
            Iterator<JsonNode> iter = rootNode.iterator();

            ObjectNode currentNode;
            MovieRecord movieRec;
            int counter = 0;
            while (iter.hasNext()) {
                currentNode = (ObjectNode) iter.next();

                // extract the primary key
                int year = currentNode.path("year").asInt();
                String title = currentNode.path("title").asText();

                movieRec = new MovieRecord(currentNode);
                try {
                    counter++;
                    createRow(dynamoDBMapper, movieRec);
                    System.out.println("PutItem succeeded: " + counter + "[" + year + " " + title + "]");

                }
                catch (Exception e) {
                    System.err.println("Unable to add movie: " + year + " " + title);
                    System.err.println(e.getMessage());
                    break;
                }
            }
            parser.close();
            System.out.println("Loaded " + counter + " items");
        } catch (JsonParseException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }
}
//Copyright 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.
//Licensed under the Apache License, Version 2.0.

