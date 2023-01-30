package com.cpen491.remote_mobility_monitoring.function.handler.patient;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import com.google.gson.Gson;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.generateInternalServerErrorResponse;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.generateResponse;

public class UpdatePatientDeviceHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final PatientService patientService;
    private final Gson gson;

    public UpdatePatientDeviceHandler() {
        Config config = Config.instance();
        this.patientService = config.patientService();
        this.gson = config.gson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            String patientId = request.getPathParameters().get(Const.PATIENT_ID_NAME);
            UpdatePatientDeviceRequestBody requestBody = UpdatePatientDeviceRequestBody.builder()
                    .patientId(patientId)
                    .build();
            UpdatePatientDeviceResponseBody responseBody = patientService.updatePatientDevice(requestBody);
            return generateResponse(HandlerUtils.StatusCode.OK, gson.toJson(responseBody));
        } catch (IllegalArgumentException | NullPointerException e) {
            return generateResponse(HandlerUtils.StatusCode.BAD_REQUEST, e.getMessage());
        } catch (RecordDoesNotExistException e) {
            return generateResponse(HandlerUtils.StatusCode.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return generateInternalServerErrorResponse();
        }
    }
}
