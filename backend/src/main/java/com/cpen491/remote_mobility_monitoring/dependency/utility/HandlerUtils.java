package com.cpen491.remote_mobility_monitoring.dependency.utility;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.InvalidMetricsException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.dependency.exception.CognitoException;
import com.cpen491.remote_mobility_monitoring.dependency.exception.InsufficientPermissionException;
import com.cpen491.remote_mobility_monitoring.dependency.exception.InvalidAuthCodeException;
import com.cpen491.remote_mobility_monitoring.dependency.exception.InvalidAuthorizationException;
import com.cpen491.remote_mobility_monitoring.dependency.exception.SesException;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class HandlerUtils {
    public enum StatusCode {
        OK(200),
        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        NOT_FOUND(404),
        INTERNAL_SERVER_ERROR(500);

        public final int code;

        StatusCode(int code) {
            this.code = code;
        }
    }

    private static final String MALFORMED_REQUEST_ERROR_MESSAGE = "Request is malformed";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "Sorry something went wrong";

    public static APIGatewayProxyResponseEvent processApiGatewayRequest(Function<APIGatewayProxyRequestEvent, String> func,
                                                                        APIGatewayProxyRequestEvent request) {
        try {
            String responseBody = func.apply(request);
            return generateApiGatewayResponse(StatusCode.OK, responseBody);
        } catch (IllegalArgumentException | NullPointerException | DuplicateRecordException e) {
            log.error("Got {} error {}, responding with bad request", e.getClass(), e.getMessage());
            return generateApiGatewayResponse(StatusCode.BAD_REQUEST, e.getMessage());
        } catch (JsonSyntaxException e) {
            log.error("Got {} error {}, responding with bad request", e.getClass(), e.getMessage());
            return generateApiGatewayResponse(StatusCode.BAD_REQUEST, MALFORMED_REQUEST_ERROR_MESSAGE);
        } catch (InvalidMetricsException | CognitoException | SesException e) {
            Throwable cause = e.getCause();
            log.error("Got {} due to {} with message {}, responding with bad request", e.getClass(), cause.getClass(), cause.getMessage());
            return generateApiGatewayResponse(StatusCode.BAD_REQUEST, cause.getMessage());
        } catch (RecordDoesNotExistException e) {
            log.error("Got {} error {}, responding with not found", e.getClass(), e.getMessage());
            return generateApiGatewayResponse(StatusCode.NOT_FOUND, e.getMessage());
        } catch (InvalidAuthorizationException | InvalidAuthCodeException e) {
            log.error("Got {} error {}, responding with unauthorized", e.getClass(), e.getMessage());
            return generateApiGatewayResponse(StatusCode.UNAUTHORIZED, e.getMessage());
        } catch (InsufficientPermissionException e) {
            log.error("Got {} error {}, responding with forbidden", e.getClass(), e.getMessage());
            return generateApiGatewayResponse(StatusCode.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            log.error("Got {} error {} with cause {}, responding with internal server error",
                    e.getClass(), e.getMessage(), e.getCause());
            return generateApiGatewayResponse(StatusCode.INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MESSAGE);
        }
    }

    private static APIGatewayProxyResponseEvent generateApiGatewayResponse(StatusCode statusCode, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,X-Amz-User-Agent");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "OPTIONS,GET,PUT,POST,DELETE,PATCH,HEAD");
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode.code)
                .withHeaders(headers)
                .withBody(body);
    }

    public static String processGenericRequest(Function<Map<String, String>, String> func, Map<String, String> request) {
        try {
            return func.apply(request);
        } catch (Exception e) {
            log.error("Got {} error {} with cause{}", e.getClass(), e.getMessage(), e.getCause());
            return null;
        }
    }
}
