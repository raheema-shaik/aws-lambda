package com.task05;
 
import java.time.Instant;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
 
@LambdaHandler(lambdaName = "api_handler",
  roleName = "api_handler-role",
  isPublishVersion = false,
  logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
 
  private static final String TABLE_NAME = "Events";
  private final AmazonDynamoDB ddbClient = AmazonDynamoDBClientBuilder.defaultClient();
  private final DynamoDB dynamoDB = new DynamoDB(ddbClient);
 
  @Override
  public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
      APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
      try {
    	  String requestBody = input.getBody();
    	  JsonObject jsonObject = new Gson().fromJson(requestBody, JsonObject.class);
    	  int principalId = jsonObject.get("principalId").getAsInt();
    	  JsonObject content = jsonObject.getAsJsonObject("content");
    	  
           Table table = dynamoDB.getTable(TABLE_NAME);
           Item item = new Item()
               .withPrimaryKey("id", UUID.randomUUID().toString())
               .withInt("principalId", principalId)
               .withString("createdAt", Instant.now().toString())
               .with("body", content);
 
           table.putItem(new PutItemSpec().withItem(item));
           JsonObject eventJson = new JsonObject();
           eventJson.addProperty("id", item.getString("id"));
           eventJson.addProperty("principalId", principalId);
           eventJson.addProperty("createdAt", Instant.now().toString());
           eventJson.add("body", content);

           JsonObject responseJson = new JsonObject();
           responseJson.addProperty("statusCode", 201);
           responseJson.add("event", eventJson);

           response.setStatusCode(201);
           response.setBody(responseJson.toString());
      } catch (ResourceNotFoundException e) {
           response.setStatusCode(500);
           response.setBody("Error: DynamoDB table 'Events' not found");
      } catch (Exception e) {
           response.setStatusCode(500);
           response.setBody("Error saving event to DynamoDB: " + e.getMessage());
      }
      return response;
  }
}
 