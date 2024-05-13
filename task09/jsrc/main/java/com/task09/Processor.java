package com.task09;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.TracingMode;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

@LambdaHandler(lambdaName = "processor",
	roleName = "processor-role",
	isPublishVersion = false,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED,
	tracingMode = TracingMode.Active
)
@LambdaUrlConfig(authType = AuthType.NONE, invokeMode = InvokeMode.BUFFERED)
@DynamoDbTriggerEventSource(batchSize = 10, targetTable = "Weather")
public class Processor implements RequestHandler<Map<String,Object>, Map<String, Object>> {

	private final Table eventsTable;
	private final Table eventsTable2;
	public Processor() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
		DynamoDB dynamoDB = new DynamoDB(client);
		eventsTable = dynamoDB.getTable("cmtr-55717e2b-Weather-test");
		eventsTable2 = dynamoDB.getTable("cmtr-55717e2b-Weather");
	}

	public Map<String, Object> handleRequest(Map<String,Object> request, Context context) {
		try {
			
			OpenMeteoAPI weather = new OpenMeteoAPI();
	        
			Item eventItem = new Item()
					.withPrimaryKey("id", UUID.randomUUID().toString())
					.withJSON("forecast", weather.getWeatherForecast());
			
			eventsTable.putItem(eventItem);
			eventsTable2.putItem(eventItem);
			
			Map<String, Object> response = new HashMap<>();
			response.put("statusCode", 201);
			response.put("body", "added succesfully"); // Return the created event

			return response;
		} catch (Exception e) {
			System.out.println(e.toString());
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("statusCode", 500);
			errorResponse.put("error", "Internal Server Error");
			return errorResponse;
		}
	}
}
