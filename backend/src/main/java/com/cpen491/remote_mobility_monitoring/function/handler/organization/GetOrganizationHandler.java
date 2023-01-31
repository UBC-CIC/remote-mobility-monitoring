package com.cpen491.remote_mobility_monitoring.function.handler.organization;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.OrganizationService;
import com.google.gson.Gson;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.process;

public class GetOrganizationHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final OrganizationService organizationService;
    private final Gson gson;

    public GetOrganizationHandler() {
        Config config = Config.instance();
        this.organizationService = config.organizationService();
        this.gson = config.gson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return process((request) -> {
            String organizationId = request.getPathParameters().get(Const.ORGANIZATION_ID_NAME);
            GetOrganizationRequestBody requestBody = GetOrganizationRequestBody.builder()
                    .organizationId(organizationId)
                    .build();
            GetOrganizationResponseBody responseBody = organizationService.getOrganization(requestBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
