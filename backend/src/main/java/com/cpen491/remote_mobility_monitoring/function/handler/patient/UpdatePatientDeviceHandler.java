package com.cpen491.remote_mobility_monitoring.function.handler.patient;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class UpdatePatientDeviceHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final PatientService patientService;
    private final Gson gson;

    public UpdatePatientDeviceHandler() {
        Config config = Config.instance();
        this.patientService = config.patientService();
        this.gson = config.gson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Update Patient device request with path parameters: {}", requestEvent.getPathParameters());
        return processApiGatewayRequest((request) -> {
            String patientId = request.getPathParameters().get(Const.PATIENT_ID_NAME);
            UpdatePatientDeviceRequestBody requestBody = UpdatePatientDeviceRequestBody.builder()
                    .patientId(patientId)
                    .build();
            UpdatePatientDeviceResponseBody responseBody = patientService.updatePatientDevice(requestBody);
            log.info("Responding to Update Patient device request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}