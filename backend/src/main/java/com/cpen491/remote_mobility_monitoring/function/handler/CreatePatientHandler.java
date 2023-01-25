package com.cpen491.remote_mobility_monitoring.function.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.CreatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.CreatePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import com.google.gson.Gson;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.generateInternalServerErrorResponse;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.generateResponse;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.StatusCode;

// TODO: decide which logger to use
public class CreatePatientHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final PatientService patientService;
    private final Gson gson;

    public CreatePatientHandler() {
        Config config = Config.instance();
        this.patientService = config.patientService();
        this.gson = config.gson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            CreatePatientRequestBody requestBody = gson.fromJson(request.getBody(), CreatePatientRequestBody.class);
            CreatePatientResponseBody responseBody = patientService.createPatient(requestBody);
            return generateResponse(StatusCode.OK, gson.toJson(responseBody));
        } catch (IllegalArgumentException | NullPointerException | DuplicateRecordException e) {
            return generateResponse(StatusCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            // TODO: log this
            return generateInternalServerErrorResponse();
        }
    }
}
