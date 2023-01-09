package ua.zakharov.aws;


import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2ProxyResponseEvent;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ua.zakharov.aws.model.MessageBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AwsController implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public APIGatewayProxyResponseEvent  handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Event's body before gson str: " + requestEvent.toString() + "\n");
        logger.log("Event's body before gson body: " + requestEvent.getBody() + "\n");
        MessageBody jsonEvent = gson.fromJson(requestEvent.getBody(), MessageBody.class);
        logger.log("Event's body after gson: " + jsonEvent + "\n");
        sendAmazonEmail(jsonEvent.toString(), context);
        //String json = gson.toJson(stringStringMap);
        //MessageBody messageBody = gson.fromJson(json, MessageBody.class);
       // String bodyResponse = messageBody.toString();
      //  logger.log(bodyResponse + "\n");
//        String line = stringStringMap.keySet()
//                .stream().map(key -> key + "=" + stringStringMap.get(key))
//                .collect(Collectors.joining(", ", "{", "}"));
//        logger.log("Enent Json: " + line + "\n");
//        logger.log("Sending email..." + "\n");
//        sendAmazonEmail(line, context);
//        response.put("statuscode", 200);
//        response.put("headers", "{'Content-Type': 'application/json'}");
//        response.put("body", json);
//        response.put("isBase64Encoded", false);
        return createResponse(jsonEvent.toString());
    }

    private APIGatewayProxyResponseEvent createResponse(String body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setIsBase64Encoded(false);
        response.setStatusCode(200);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html");
        response.setHeaders(headers);
        response.setBody(body);
        return response;
    }
    private void sendAmazonEmail(String content, Context context) {
        LambdaLogger logger = context.getLogger();
        Regions regions = Regions.US_EAST_1;
        try {
            AmazonSimpleEmailService client =
                    AmazonSimpleEmailServiceClientBuilder.standard().withRegion(regions).build();

            SendEmailRequest sendEmailRequest = new SendEmailRequest();
            sendEmailRequest.setSource("fencingmylove@gmail.com");

            Destination destination = new Destination(List.of(
                    "vadimgudym@gmail.com",
                    "andreyb@csiworks.net"));
            sendEmailRequest.setDestination(destination);

            Content subject = new Content("The letter from Amazon SES");
            Content text = new Content(content);

            Body body = new Body(text);
            Message message = new Message(subject, body);

            sendEmailRequest.setMessage(message);

            SendEmailResult response = client.sendEmail(sendEmailRequest);
            if(response.getMessageId() != null) {
                logger.log("Email was sent\n");
            } else {
                logger.log("Email was not sent\n");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }


    }


}
