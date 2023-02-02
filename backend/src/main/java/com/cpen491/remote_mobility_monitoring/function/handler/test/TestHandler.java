package com.cpen491.remote_mobility_monitoring.function.handler.test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.CreateCognitoUserRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.CreateCognitoUserResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.CaregiverService;
import com.google.gson.Gson;

import java.util.HashMap;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.*;

public class TestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final Gson gson;
    private final CaregiverService caregiverService;

    public TestHandler() {
        Config config = Config.instance();
        this.gson = config.gson();
        this.caregiverService = config.caregiverService();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            CreateCognitoUserRequestBody requestBody = gson.fromJson(request.getBody(), CreateCognitoUserRequestBody.class);
            CreateCognitoUserResponseBody responseBody = caregiverService.createCognitoUser(requestBody);
            return generateResponse(StatusCode.OK, gson.toJson(responseBody));
        } catch (IllegalArgumentException | NullPointerException | DuplicateRecordException e) {
            return generateResponse(StatusCode.BAD_REQUEST, e.getMessage());
        } catch (RecordDoesNotExistException e) {
            return generateResponse(StatusCode.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return generateInternalServerErrorResponse();
        }
    }
}
