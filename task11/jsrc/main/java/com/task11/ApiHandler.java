package com.task11;

//import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.lambda.runtime.RequestHandler;
//import com.syndicate.deployment.annotations.lambda.LambdaHandler;
//import com.syndicate.deployment.model.RetentionSetting;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@LambdaHandler(lambdaName = "api_handler",
//	roleName = "api_handler-role",
//	isPublishVersion = true,
//	aliasName = "${lambdas_alias_name}",
//	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
//)
//public class ApiHandler implements RequestHandler<Object, Map<String, Object>> {
//
//	public Map<String, Object> handleRequest(Object request, Context context) {
//		System.out.println("Hello from lambda");
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		resultMap.put("statusCode", 200);
//		resultMap.put("body", "Hello from Lambda");
//		return resultMap;
//	}
//}
//
//













import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;
import com.task11.handler.ReservationsHandler;
import com.task11.handler.TablesHandler;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolClientsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolClientsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserNotFoundException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserPoolClientDescription;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserPoolDescriptionType;
 
@LambdaHandler(lambdaName = "api_handler",

		roleName = "api_handler-role",

		isPublishVersion = false,

		logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED

)

public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final String USER_POOL_NAME = "cmtr-55717e2b-simple-booking-userpool-test";

	private final CognitoIdentityProviderClient cognitoClient;

	private static final String USER_POOL_ID = getUserPoolId();

	private static final String APP_CLIENT_ID = getClientId();

	public ApiHandler() {

		cognitoClient = CognitoIdentityProviderClient.create();

	}

	public static String getUserPoolId() {

		CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.create();

		ListUserPoolsRequest listUserPoolsRequest = ListUserPoolsRequest.builder().maxResults(10).build();

		ListUserPoolsResponse listUserPoolsResponse = cognitoClient.listUserPools(listUserPoolsRequest);

		String userPoolId = listUserPoolsResponse.userPools().get(0).id();

		for(UserPoolDescriptionType userPool : listUserPoolsResponse.userPools()) {

			if (userPool.name().equals(USER_POOL_NAME)) {

				userPoolId = userPool.id();

				System.out.println("User Pool Id is: " + userPoolId);

				break;

			}

		}

		return userPoolId;

	}
 
	public static String getClientId() {

		CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.create();

		ListUserPoolClientsRequest listUserPoolClientsRequest = ListUserPoolClientsRequest.builder()

				.userPoolId(USER_POOL_ID).maxResults(10).build();

		ListUserPoolClientsResponse listUserPoolClientsResponse = cognitoClient

				.listUserPoolClients(listUserPoolClientsRequest);

		String clientId = "";

		for (UserPoolClientDescription userPoolClient : listUserPoolClientsResponse.userPoolClients()) {

			if (userPoolClient.clientName().equals("client-app")) {

				clientId = userPoolClient.clientId();

				System.out.println("Client Id is: " + clientId);

				break;

			}

		}

		return clientId;

	}
 
	public  APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

		try {

			System.out.println(event);

			System.out.println(event.getResource());

			switch (event.getResource()) {

				case "/signup":

					return handleSignUp(event);

				case "/signin":

					return handleSignIn(event);

				case "/tables":

					return handleTables(event);

				case "/tables/{tableId}":

					return handleTables(event);

				case "/reservations":

					return handleReservations(event);

				default:

					break;

			}

		} catch (Exception e) {

			System.out.println(e);

		}

		Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");
        headers.put("Accept-Version", "*");
        
		return new APIGatewayProxyResponseEvent().withStatusCode(400).withHeaders(headers);

	}

	private APIGatewayProxyResponseEvent handleReservations(APIGatewayProxyRequestEvent event) {

		if (event.getHttpMethod().equalsIgnoreCase("GET")) {

			return new ReservationsHandler().getAllReservations();

		} else if (event.getHttpMethod().equalsIgnoreCase("POST")) {

			return new ReservationsHandler().postReservations(event);

		}
		
		Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");
        headers.put("Accept-Version", "*");

		return new APIGatewayProxyResponseEvent().withStatusCode(400).withHeaders(headers);

	}

	private APIGatewayProxyResponseEvent handleTables(APIGatewayProxyRequestEvent event) {
 
		if(Objects.nonNull(event.getPathParameters())){

			return new TablesHandler().getTables(event);

		} else if (event.getHttpMethod().equalsIgnoreCase("GET")) {

			return new TablesHandler().getAllTables();

		} else if (event.getHttpMethod().equalsIgnoreCase("POST")) {

			return new TablesHandler().postTables(event);

		}
		
		Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");
        headers.put("Accept-Version", "*");

		return new APIGatewayProxyResponseEvent().withStatusCode(400).withHeaders(headers);

	}

	public APIGatewayProxyResponseEvent handleSignUp(APIGatewayProxyRequestEvent event) {

		Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");
        headers.put("Accept-Version", "*");
        
		try {

			Map<String, String> input = new Gson().fromJson(event.getBody(), HashMap.class);

			System.out.println("request body is parsed");

			String firstName = input.get("firstName");

			String lastName = input.get("lastName");

			String email = input.get("email");

			String password = input.get("password");

			// Validate the inputs here! (Check the email format and password rules)

			System.out.println("Creating user_request");

			AdminCreateUserRequest user_request = AdminCreateUserRequest.builder()

					.userPoolId(USER_POOL_ID)

					.username(email)

					.messageAction("SUPPRESS")

					.build();

			cognitoClient.adminCreateUser(user_request);

			AdminSetUserPasswordRequest passwordRequest = AdminSetUserPasswordRequest.builder()

					.username(email)

					.password(password)

					.userPoolId(USER_POOL_ID)

					.permanent(true)

					.build();

			cognitoClient.adminSetUserPassword(passwordRequest);
			
			return new APIGatewayProxyResponseEvent().withStatusCode(200).withHeaders(headers);

		} catch (Exception e) {
		
			return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("Failed to sign up!").withHeaders(headers);

		}

	}

	public APIGatewayProxyResponseEvent handleSignIn(APIGatewayProxyRequestEvent request) {

		Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");
        headers.put("Accept-Version", "*");
        
        
		Map<String, String> input = new Gson().fromJson(request.getBody(), HashMap.class);

		CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder().region(Region.EU_CENTRAL_1).build();

		Map<String,String> authParameters = new HashMap<>();

		authParameters.put("USERNAME", input.get("email"));

		authParameters.put("PASSWORD", input.get("password"));

		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

		InitiateAuthRequest authRequest = InitiateAuthRequest.builder()

				.authFlow(AuthFlowType.USER_PASSWORD_AUTH)

				.authParameters(authParameters)

				.clientId(APP_CLIENT_ID)

				.build();

		try {

			InitiateAuthResponse authResponse = cognitoClient.initiateAuth(authRequest);

			response.setBody("{ \"accessToken\": \"" + authResponse.authenticationResult().accessToken() + "\"}");

			response.setStatusCode(200);
			
			response.setHeaders(headers);

		} catch (NotAuthorizedException e) {

			response.setStatusCode(400);
			
			response.setHeaders(headers);

		} catch (UserNotFoundException e) {

			response.setStatusCode(400);
			
			response.setHeaders(headers);

		} catch (Exception e) {

			response.setStatusCode(400);
			
			response.setHeaders(headers);

		}

		
		return response;

	}

}