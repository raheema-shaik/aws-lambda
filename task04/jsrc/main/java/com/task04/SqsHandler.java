package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
 
@LambdaHandler(lambdaName = "sqs_handler",
	roleName = "sqs_handler-role",
	isPublishVersion = false,
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SqsTriggerEventSource(targetQueue = "async_queue", batchSize = 10)
public class SqsHandler implements RequestHandler<SQSEvent, String> {

	public String handleRequest(SQSEvent event, Context context) {
		for(SQSEvent.SQSMessage message : event.getRecords()) {
			String messageBody = message.getBody();
			System.out.println("Received message: " + messageBody);
		}
		return "Executed successfully from lambda sqs handler";
		
	}
}
