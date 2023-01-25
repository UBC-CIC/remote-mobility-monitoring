package com.cpen491.remote_mobility_monitoring.dependency.utility;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

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

    public static APIGatewayProxyResponseEvent generateResponse(StatusCode statusCode, String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode.code)
                .withBody(body);
    }

    public static APIGatewayProxyResponseEvent generateInternalServerErrorResponse() {
        return generateResponse(StatusCode.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
    }
}
