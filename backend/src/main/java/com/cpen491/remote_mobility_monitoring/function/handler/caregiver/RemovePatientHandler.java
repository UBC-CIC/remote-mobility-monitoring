package com.cpen491.remote_mobility_monitoring.function.handler.caregiver;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.RemovePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.RemovePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.CaregiverService;
import com.google.gson.Gson;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.generateInternalServerErrorResponse;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.generateResponse;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.StatusCode;

public class RemovePatientHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final CaregiverService caregiverService;
    private final Gson gson;

    public RemovePatientHandler() {
        Config config = Config.instance();
        this.caregiverService = config.caregiverService();
        this.gson = config.gson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            String caregiverId = request.getPathParameters().get(Const.CAREGIVER_ID_NAME);
            String patientId = request.getPathParameters().get(Const.PATIENT_ID_NAME);
            RemovePatientRequestBody requestBody = RemovePatientRequestBody.builder()
                    .caregiverId(caregiverId)
                    .patientId(patientId)
                    .build();
            RemovePatientResponseBody responseBody = caregiverService.removePatient(requestBody);
            return generateResponse(StatusCode.OK, gson.toJson(responseBody));
        } catch (IllegalArgumentException | NullPointerException e) {
            return generateResponse(StatusCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return generateInternalServerErrorResponse();
        }
    }
}
