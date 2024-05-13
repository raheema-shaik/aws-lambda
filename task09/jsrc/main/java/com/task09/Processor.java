package com.task09;

import java.time.LocalDateTime;
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
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

@LambdaHandler(lambdaName = "processor",
	roleName = "processor-role",
	isPublishVersion = false,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(authType = AuthType.NONE, invokeMode = InvokeMode.BUFFERED)

//public class Processor implements RequestHandler<Object, Map<String, Object>> {
//
//	public Map<String, Object> handleRequest(Object request, Context context) {
//
//			OpenMeteoAPI weather = new OpenMeteoAPI();
//			
//	        Map<String, Object> resultMap = new HashMap<String, Object>();
//
//			resultMap.put("statusCode", 200);
//
//			resultMap.put("body", weather.getWeatherForecast());
//
//			return resultMap;
//
//		}
//}

public class Processor implements RequestHandler<Map<String,Object>, Map<String, Object>> {

	//private final Table eventsTable;
	private final Table eventsTable2;
	public Processor() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
		DynamoDB dynamoDB = new DynamoDB(client);
		//eventsTable = dynamoDB.getTable("cmtr-55717e2b-Weather-test");
		System.out.println("before calling table");
		eventsTable2 = dynamoDB.getTable("cmtr-55717e2b-Weather");
		System.out.println("after calling table");
	}

	public Map<String, Object> handleRequest(Map<String,Object> request, Context context) {
		try {
			System.out.println("before api");
			OpenMeteoAPI weather = new OpenMeteoAPI();
	        
			Item eventItem = new Item()
					.withPrimaryKey("id", UUID.randomUUID().toString())
					.withJSON("forecast", weather.getWeatherForecast());
			System.out.println("after api response and item");
			//eventsTable.putItem(eventItem);
			eventsTable2.putItem(eventItem);
			System.out.println("after putting to db");
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
