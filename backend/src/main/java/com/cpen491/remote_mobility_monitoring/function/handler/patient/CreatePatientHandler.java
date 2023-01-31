package com.cpen491.remote_mobility_monitoring.function.handler.patient;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import com.google.gson.Gson;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.process;

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
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return process((request) -> {
            CreatePatientRequestBody requestBody = gson.fromJson(request.getBody(), CreatePatientRequestBody.class);
            CreatePatientResponseBody responseBody = patientService.createPatient(requestBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
