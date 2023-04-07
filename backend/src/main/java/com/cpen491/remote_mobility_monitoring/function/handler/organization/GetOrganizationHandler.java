package com.cpen491.remote_mobility_monitoring.function.handler.organization;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.handler.HandlerParent;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationResponseBody;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class GetOrganizationHandler extends HandlerParent implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Get Organization request with path parameters: {}", requestEvent.getPathParameters());
        return processApiGatewayRequest((request) -> {
            String organizationId = request.getPathParameters().get(Const.ORGANIZATION_ID_NAME);
            GetOrganizationRequestBody requestBody = GetOrganizationRequestBody.builder()
                    .organizationId(organizationId)
                    .build();
            GetOrganizationResponseBody responseBody = organizationService.getOrganization(requestBody);
            log.info("Responding to Get Organization request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
