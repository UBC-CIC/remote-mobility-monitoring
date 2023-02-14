package com.cpen491.remote_mobility_monitoring.function.handler.patient;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.handler.HandlerParent;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientResponseBody;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class DeletePatientHandler extends HandlerParent implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>  {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Delete Patient request with path parameters: {}", requestEvent.getPathParameters());
        return processApiGatewayRequest((request) -> {
            String patientId = request.getPathParameters().get(Const.PATIENT_ID_NAME);
            DeletePatientRequestBody requestBody = DeletePatientRequestBody.builder()
                    .patientId(patientId)
                    .build();
            DeletePatientResponseBody responseBody = patientService.deletePatient(requestBody);
            log.info("Responding to Delete Patient request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
