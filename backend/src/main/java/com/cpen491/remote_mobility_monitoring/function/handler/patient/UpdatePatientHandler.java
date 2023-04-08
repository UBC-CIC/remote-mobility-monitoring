package com.cpen491.remote_mobility_monitoring.function.handler.patient;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.dependency.utility.JwtUtils;
import com.cpen491.remote_mobility_monitoring.function.handler.HandlerParent;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientResponseBody;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class UpdatePatientHandler extends HandlerParent implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Update Patient request with path parameters: {} and body: {}",
                requestEvent.getPathParameters(), requestEvent.getBody());
        return processApiGatewayRequest((request) -> {
            String rawId = JwtUtils.getIdFromHeader(request.getHeaders(), gson);
            String patientId = request.getPathParameters().get(Const.PATIENT_ID_NAME);
            authService.selfCheckThrow(rawId, patientId);
            UpdatePatientRequestBody requestBody = gson.fromJson(request.getBody(), UpdatePatientRequestBody.class);
            requestBody.setPatientId(patientId);
            UpdatePatientResponseBody responseBody = patientService.updatePatient(requestBody);
            log.info("Responding to Update Patient request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
