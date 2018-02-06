package erehwon.shadowlands.dynamodbdemo1;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class loadTableApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", Regions.AP_SOUTHEAST_2.getName()))
                .build();

            DynamoDB dynamoDB = new DynamoDB(client);

            Table table = dynamoDB.getTable("Movies");

            JsonParser parser;
            JsonNode rootNode;
			try {
//				parser = new JsonFactory().createParser(new File("..\\..\\..\\..\\resources\\moviedata.json"));
				parser = new JsonFactory().createParser(new File("D:\\Warehouse\\git_clones\\AWS_Demos\\dynamodbdemo1\\src\\main\\resources\\moviedata.json"));
	            rootNode = new ObjectMapper().readTree(parser);
	            Iterator<JsonNode> iter = rootNode.iterator();

	            ObjectNode currentNode;

	            while (iter.hasNext()) {
	                currentNode = (ObjectNode) iter.next();

	                int year = currentNode.path("year").asInt();
	                String title = currentNode.path("title").asText();

	                try {
	                    table.putItem(new Item().withPrimaryKey("year", year, "title", title).withJSON("info",
	                        currentNode.path("info").toString()));
	                    System.out.println("PutItem succeeded: " + year + " " + title);

	                }
	                catch (Exception e) {
	                    System.err.println("Unable to add movie: " + year + " " + title);
	                    System.err.println(e.getMessage());
	                    break;
	                }
	            }
	            parser.close();
			} catch (JsonParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}


	}

}
