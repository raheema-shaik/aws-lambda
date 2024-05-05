package com.task05;
 
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
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
           String body = input.getBody();
           String principalId = ""; 
           String content = "";             
           Table table = dynamoDB.getTable(TABLE_NAME);
           Item item = new Item()
               .withPrimaryKey("id", String.valueOf(System.currentTimeMillis()))
               .withString("principalId", principalId)
               .withString("content", content)
               .withString("createdAt", String.valueOf(System.currentTimeMillis()));
 
           table.putItem(new PutItemSpec().withItem(item));
 
           response.setStatusCode(200);
           response.setBody(String.format("Event created successfully with ID: %s", item.getString("id")));
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
 