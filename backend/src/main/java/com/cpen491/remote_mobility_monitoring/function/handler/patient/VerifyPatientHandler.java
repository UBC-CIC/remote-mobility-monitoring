package com.cpen491.remote_mobility_monitoring.function.handler.patient;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.VerifyPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.VerifyPatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import com.google.gson.Gson;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.process;

public class VerifyPatientHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final PatientService patientService;
    private final Gson gson;

    public VerifyPatientHandler() {
        Config config = Config.instance();
        this.patientService = config.patientService();
        this.gson = config.gson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return process((request) -> {
            String patientId = request.getPathParameters().get(Const.PATIENT_ID_NAME);
            VerifyPatientRequestBody requestBody = gson.fromJson(request.getBody(), VerifyPatientRequestBody.class);
            requestBody.setPatientId(patientId);
            VerifyPatientResponseBody responseBody = patientService.verifyPatient(requestBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
