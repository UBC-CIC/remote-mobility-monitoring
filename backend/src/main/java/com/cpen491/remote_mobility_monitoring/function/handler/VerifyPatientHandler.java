package com.cpen491.remote_mobility_monitoring.function.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.VerifyPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.VerifyPatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import com.google.gson.Gson;

public class VerifyPatientHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final PatientService patientService;
    private final Gson gson;

    public VerifyPatientHandler() {
        Config config = Config.instance();
        this.patientService = config.patientService();
        this.gson = config.gson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            VerifyPatientRequestBody body = gson.fromJson(request.getBody(), VerifyPatientRequestBody.class);
            patientService.verifyPatient(body);
        } catch (Exception e) {
            // TODO: catch more granular exception
            e.printStackTrace();
        }
        return null;
    }
}
