package com.cpen491.remote_mobility_monitoring.dependency.utility;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.InvalidAuthCodeException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;

import java.util.Map;
import java.util.function.Function;

public class HandlerUtils {
    public enum StatusCode {
        OK(200),
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        NOT_FOUND(404),
        INTERNAL_SERVER_ERROR(500);

        public final int code;

        StatusCode(int code) {
            this.code = code;
        }
    }

    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Sorry something went wrong";

    public static APIGatewayProxyResponseEvent processApiGatewayRequest(Function<APIGatewayProxyRequestEvent, String> func,
                                                                        APIGatewayProxyRequestEvent request) {
        try {
            String responseBody = func.apply(request);
            return generateApiGatewayResponse(StatusCode.OK, responseBody);
        } catch (IllegalArgumentException | NullPointerException | DuplicateRecordException e) {
            return generateApiGatewayResponse(StatusCode.BAD_REQUEST, e.getMessage());
        } catch (RecordDoesNotExistException e) {
            return generateApiGatewayResponse(StatusCode.NOT_FOUND, e.getMessage());
        } catch (InvalidAuthCodeException e) {
            return generateApiGatewayResponse(StatusCode.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            // TODO: log this
            return generateApiGatewayResponse(StatusCode.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
        }
    }

    private static APIGatewayProxyResponseEvent generateApiGatewayResponse(StatusCode statusCode, String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode.code)
                .withBody(body);
    }

    public static void processGenericRequest(Function<Map<String, String>, String> func, Map<String, String> request) {
        String message;
        try {
            message = func.apply(request);
        } catch (Exception e) {
            message = e.getMessage();
        }
        // TODO: log this instead
        System.out.println(message);
    }
}
