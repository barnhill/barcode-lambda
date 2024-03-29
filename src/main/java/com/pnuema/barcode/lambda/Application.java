package com.pnuema.barcode.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.pnuema.barcode.lambda.json.Json;
import com.pnuema.barcode.lambda.models.PostRequest;
import com.pnuema.barcode.lambda.models.Request;
import com.pnuema.barcode.lambda.models.Response;
import com.pnuema.java.barcode.Barcode;

import java.awt.*;
import java.io.IOException;
import java.util.*;

public class Application implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final Map<String, Barcode.TYPE> symbologies = new LinkedHashMap<>();

    public static void main( final String[] args ) {}

    /**
     * Entry point for handling request from the API.
     *
     * @param event the event request.
     * @param context the AWS lambda context object.
     * @return a response code.
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        LambdaLogger logger = context.getLogger();
        try {
            logger.log("Event: " + event);
            logger.log("Request Type: " + event.getHttpMethod());
            Request request;
            switch (event.getHttpMethod().toLowerCase()) {
                case "get":
                    logger.log("Request Type: GET");
                    request = new Request();
                    if (event.getPathParameters() != null && event.getPathParameters().containsKey("data")) {
                        request.setData(event.getPathParameters().get("data"));
                    } else {
                        request.setData(event.getQueryStringParameters().get("data"));
                    }
                    logger.log("Data: " + request.getData());

                    if (event.getPathParameters() != null && event.getPathParameters().containsKey("symbology")) {
                        request.setSymbology(event.getPathParameters().get("symbology"));
                    } else {
                        request.setSymbology(event.getQueryStringParameters().get("symbology"));
                    }
                    logger.log("Symbology: " + request.getSymbology());

                    if (event.getQueryStringParameters() != null) {
                        String width = event.getQueryStringParameters().get("width");
                        request.setWidth(width);
                        logger.log("Width: " + request.getWidth());

                        String height = event.getQueryStringParameters().get("height");
                        request.setHeight(height);
                        logger.log("Height: " + request.getHeight());

                        String format = event.getQueryStringParameters().get("format");
                        request.setFormat(format);
                        logger.log("Format: " + request.getFormat());
                    }
                    break;
                case "post":
                    logger.log("Request Type: POST");
                    try {
                        PostRequest postRequest = Json.fromJson(PostRequest.class, event.getIsBase64Encoded() ? new String(Base64.getDecoder().decode(event.getBody())) : event.getBody());
                        request = postRequest.getRequest();
                    } catch (IOException | NullPointerException e) {
                        return new APIGatewayProxyResponseEvent().withBody(new Response("Error (Parse request): " + event.getBody()).toJson()).withStatusCode(500);
                    }
                    break;
                default:
                    return new APIGatewayProxyResponseEvent().withBody(new Response("Error (Invalid HTTP request method): " + event.getHttpMethod()).toJson()).withStatusCode(500);
            }

            APIGatewayProxyResponseEvent validationError = inputValidation(request);
            if (validationError != null) {
                return validationError;
            }

            Barcode barcode = new Barcode();
            try {
                Image image = barcode.encode(convertTypeFromString(request.getSymbology()), request.getData());

                logger.log("Image: " + image.toString());
            } catch (Exception ex) {
                logger.log("Encoding Error: " + ex);
            }

            return new APIGatewayProxyResponseEvent().withBody(
                    new Response(barcode.getRawData(),
                            barcode.getEncodedType().toString(),
                            barcode.getEncodedValue(),
                            Base64.getEncoder().encodeToString(barcode.getImageData(getImageType(request))),
                            getImageType(request).toString(),
                            barcode.getWidth(),
                            barcode.getHeight())
                            .toJson())
                    .withStatusCode(200);
        } catch (Exception ex) {
            logger.log("API Error: " + ex + ": " + Arrays.toString(ex.getStackTrace()));
            return new APIGatewayProxyResponseEvent().withBody(new Response(Json.getFormattedError(ex)).toJson()).withStatusCode(500);
        }
    }

    private APIGatewayProxyResponseEvent inputValidation (Request request) {
        //check required fields and if not present return error message
        if (request.getData() == null || request.getData().isEmpty()) {
            return new APIGatewayProxyResponseEvent().withBody(new Response("Error (Invalid data): " + request).toJson()).withStatusCode(500);
        }
        Barcode.TYPE type = convertTypeFromString(request.getSymbology());
        if (type == null) {
            return new APIGatewayProxyResponseEvent().withBody(new Response("Error (Invalid symbology): " + request.getSymbology()).toJson()).withStatusCode(500);
        }

        return null;
    }

    private Barcode.SaveTypes getImageType(Request request) {
        if (request.getFormat() == null || request.getFormat().isEmpty()) {
            return Barcode.SaveTypes.PNG;
        }
        switch (request.getFormat()) {
            case "gif":
                return Barcode.SaveTypes.GIF;
            case "png":
            default:
                return Barcode.SaveTypes.PNG;
        }
    }

    private Barcode.TYPE convertTypeFromString(String symbology) {
        //lazy load the lookup
        if (symbologies.isEmpty()) {
            symbologies.put("upca", Barcode.TYPE.UPCA);
            symbologies.put("upce", Barcode.TYPE.UPCE);
            symbologies.put("ean13", Barcode.TYPE.EAN13);
            symbologies.put("ean8", Barcode.TYPE.EAN8);
            symbologies.put("upcsupp2", Barcode.TYPE.UPC_SUPPLEMENTAL_2DIGIT);
            symbologies.put("upcsupp5", Barcode.TYPE.UPC_SUPPLEMENTAL_5DIGIT);
            symbologies.put("jan13", Barcode.TYPE.JAN13);
            symbologies.put("itf14", Barcode.TYPE.ITF14);
            symbologies.put("codabar", Barcode.TYPE.Codabar);
            symbologies.put("postnet", Barcode.TYPE.PostNet);
            symbologies.put("isbn", Barcode.TYPE.ISBN);
            symbologies.put("code11", Barcode.TYPE.CODE11);
            symbologies.put("code39", Barcode.TYPE.CODE39);
            symbologies.put("code39ext", Barcode.TYPE.CODE39Extended);
            symbologies.put("code39mod43", Barcode.TYPE.CODE39_Mod43);
            symbologies.put("logmars", Barcode.TYPE.LOGMARS);
            symbologies.put("msi", Barcode.TYPE.MSI_Mod10);
            symbologies.put("i2of5", Barcode.TYPE.Interleaved2of5);
            symbologies.put("i2of5mod10", Barcode.TYPE.Interleaved2of5_Mod10);
            symbologies.put("s2of5", Barcode.TYPE.Standard2of5);
            symbologies.put("s2of5mod10", Barcode.TYPE.Standard2of5_Mod10);
            symbologies.put("code128", Barcode.TYPE.CODE128);
            symbologies.put("code128a", Barcode.TYPE.CODE128A);
            symbologies.put("code128b", Barcode.TYPE.CODE128B);
            symbologies.put("code128c", Barcode.TYPE.CODE128C);
            symbologies.put("telepen", Barcode.TYPE.TELEPEN);
            symbologies.put("fim", Barcode.TYPE.FIM);
            symbologies.put("pharmacode", Barcode.TYPE.PHARMACODE);
        }

        return symbologies.get(symbology);
    }
}
