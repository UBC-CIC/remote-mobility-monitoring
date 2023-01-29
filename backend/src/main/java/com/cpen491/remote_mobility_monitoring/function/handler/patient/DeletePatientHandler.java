package com.cpen491.remote_mobility_monitoring.function.handler.patient;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.DeletePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import com.google.gson.Gson;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.generateInternalServerErrorResponse;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.generateResponse;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.StatusCode;

public class DeletePatientHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>  {
    private final PatientService patientService;
    private final Gson gson;

    public DeletePatientHandler() {
        Config config = Config.instance();
        this.patientService = config.patientService();
        this.gson = config.gson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            String patientId = request.getPathParameters().get(Const.PATIENT_ID_NAME);
            DeletePatientRequestBody requestBody = DeletePatientRequestBody.builder()
                    .patientId(patientId)
                    .build();
            DeletePatientResponseBody responseBody = patientService.deletePatient(requestBody);
            return generateResponse(StatusCode.OK, gson.toJson(responseBody));
        } catch (IllegalArgumentException | NullPointerException e) {
            return generateResponse(StatusCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return generateInternalServerErrorResponse();
        }
    }
}
