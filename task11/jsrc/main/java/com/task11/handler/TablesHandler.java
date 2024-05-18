package com.task11.handler;

//public class TablesHandler {
//
//}
//
//
//package com.task10.handler;

import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;

import java.util.Map;
 
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;

import com.amazonaws.services.dynamodbv2.document.Item;

import com.amazonaws.services.dynamodbv2.document.Table;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import com.amazonaws.services.dynamodbv2.model.ScanRequest;

import com.amazonaws.services.dynamodbv2.model.ScanResult;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import com.google.gson.Gson;

import com.task11.pojo.Tables;

public class TablesHandler {

    private static AmazonDynamoDB ddbClient = AmazonDynamoDBClientBuilder.defaultClient();

    private static DynamoDB dynamoDB = new DynamoDB(ddbClient);

    private static Gson gson = new Gson();

    private final String tableName = "cmtr-55717e2b-Tables-test";

    public APIGatewayProxyResponseEvent getAllTables() {

        ScanRequest scanRequest = new ScanRequest()

                .withTableName(tableName);

        ScanResult result = ddbClient.scan(scanRequest);

        List<Tables> tables = new ArrayList<>();

        for (Map<String, AttributeValue> item : result.getItems()) {

            Tables table = new Tables();

            table.setId(Integer.parseInt(item.get("id").getN()));

            table.setNumber(Integer.parseInt(item.get("number").getN()));

            table.setPlaces(Integer.parseInt(item.get("places").getN()));

            table.setVip(Boolean.parseBoolean(String.valueOf(item.get("vip"))));

            if (item.containsKey("minOrder")) {

                table.setMinOrder(Integer.parseInt(item.get("minOrder").getN()));

            }

            tables.add(table);

        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");
        headers.put("Accept-Version", "*");

        
        Map<String, List<Tables>> tablesMap = new HashMap<>();

        tablesMap.put("tables", tables);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        response.setStatusCode(200);

        response.setBody(gson.toJson(tablesMap));
        
        response.setHeaders(headers);

        return response;

    }

    public APIGatewayProxyResponseEvent postTables(APIGatewayProxyRequestEvent request) {

        System.out.println("Post Tables");

        DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

        System.out.println("Mapper created successfully");

        Tables tableItem = gson.fromJson(request.getBody(), Tables.class);

        System.out.println(tableItem.toString());

        System.out.println("Saving Tables");

        mapper.save(tableItem);

        System.out.println("Returning");
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");
        headers.put("Accept-Version", "*");


        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        response.setStatusCode(200);

        response.setBody("{\"id\": " + tableItem.getId() + "}");
        
        response.setHeaders(headers);

        return response;

    }

    public APIGatewayProxyResponseEvent getTables(APIGatewayProxyRequestEvent event) {
    	
    	 Map<String, String> headers = new HashMap<>();
         headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
         headers.put("Access-Control-Allow-Origin", "*");
         headers.put("Access-Control-Allow-Methods", "*");
         headers.put("Accept-Version", "*");

    	

        int tableId = Integer.parseInt(event.getPathParameters().get("tableId"));

        System.out.println(tableId);

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {

            Table table = dynamoDB.getTable(tableName);

            Item item = table.getItem("id", tableId);

            System.out.println(item);

            if (item == null) {

                System.out.println("Item null");

                response.setStatusCode(400);
                
                response.setHeaders(headers);

                return response;

            }

            Tables tables = new Tables();

            tables.setId(Integer.parseInt(item.getString("id")));

            tables.setNumber(Integer.parseInt(item.getString("number")));

            tables.setPlaces(Integer.parseInt(item.getString("places")));

            tables.setVip(Boolean.parseBoolean(String.valueOf(item.get("vip"))));

            if (item.isPresent("minOrder"))

                tables.setMinOrder(Integer.parseInt(item.getString("minOrder")));

            System.out.println("All set Response");

            response.setStatusCode(200);

            response.setBody(gson.toJson(tables));
            
            response.setHeaders(headers);

        } catch (Exception e) {

            response.setStatusCode(400);
            
            response.setHeaders(headers);

            System.out.println(e);

        }

        return response;

    }

}