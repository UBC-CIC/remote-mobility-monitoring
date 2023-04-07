package com.cpen491.remote_mobility_monitoring.function.handler.caregiver;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.dependency.utility.JwtUtils;
import com.cpen491.remote_mobility_monitoring.function.handler.HandlerParent;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.DeleteCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.DeleteCaregiverResponseBody;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class DeleteCaregiverHandler extends HandlerParent implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Delete Caregiver request with path parameters: {}", requestEvent.getPathParameters());
        return processApiGatewayRequest((request) -> {
            String rawId = JwtUtils.getIdFromHeader(request.getHeaders(), gson);
            authService.isAdmin(rawId);
            String caregiverId = request.getPathParameters().get(Const.CAREGIVER_ID_NAME);
            DeleteCaregiverRequestBody requestBody = DeleteCaregiverRequestBody.builder()
                    .caregiverId(caregiverId)
                    .build();
            DeleteCaregiverResponseBody responseBody = caregiverService.deleteCaregiver(requestBody);
            log.info("Responding to Delete Caregiver request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
