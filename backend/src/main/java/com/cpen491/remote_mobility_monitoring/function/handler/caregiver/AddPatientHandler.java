package com.cpen491.remote_mobility_monitoring.function.handler.caregiver;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AddPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AddPatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.CaregiverService;
import com.google.gson.Gson;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.process;

public class AddPatientHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final CaregiverService caregiverService;
    private final Gson gson;

    public AddPatientHandler() {
        Config config = Config.instance();
        this.caregiverService = config.caregiverService();
        this.gson = config.gson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return process((request) -> {
            String caregiverId = request.getPathParameters().get(Const.CAREGIVER_ID_NAME);
            String patientId = request.getPathParameters().get(Const.PATIENT_ID_NAME);
            AddPatientRequestBody requestBody = AddPatientRequestBody.builder()
                    .caregiverId(caregiverId)
                    .patientId(patientId)
                    .build();
            AddPatientResponseBody responseBody = caregiverService.addPatient(requestBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
