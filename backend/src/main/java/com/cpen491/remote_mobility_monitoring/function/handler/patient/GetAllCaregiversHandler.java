package com.cpen491.remote_mobility_monitoring.function.handler.patient;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.dependency.utility.JwtUtils;
import com.cpen491.remote_mobility_monitoring.function.handler.HandlerParent;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetAllCaregiversRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetAllCaregiversResponseBody;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class GetAllCaregiversHandler extends HandlerParent implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Get all Caregivers request with path parameters: {}", requestEvent.getPathParameters());
        return processApiGatewayRequest((request) -> {
            String rawId = JwtUtils.getIdFromHeader(request.getHeaders(), gson);
            String patientId = request.getPathParameters().get(Const.PATIENT_ID_NAME);
            if (!authService.selfCheck(rawId, patientId)) {
                authService.caregiverHasPatient(rawId, patientId);
            }
            GetAllCaregiversRequestBody requestBody = GetAllCaregiversRequestBody.builder()
                    .patientId(patientId)
                    .build();
            GetAllCaregiversResponseBody responseBody = patientService.getAllCaregivers(requestBody);
            log.info("Responding to Get all Caregivers request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
