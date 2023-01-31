package com.cpen491.remote_mobility_monitoring.function.handler.organization;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.CreateOrganizationRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.CreateOrganizationResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.OrganizationService;
import com.google.gson.Gson;

import java.util.Map;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processGenericRequest;

public class CreateOrganizationHandler implements RequestHandler<Map<String, String>, Void> {
    private final OrganizationService organizationService;
    private final Gson gson;

    public CreateOrganizationHandler() {
        Config config = Config.instance();
        this.organizationService = config.organizationService();
        this.gson = config.gson();
    }

    @Override
    public Void handleRequest(Map<String, String> event, Context context) {
        processGenericRequest((request) -> {
            CreateOrganizationRequestBody requestBody = CreateOrganizationRequestBody.builder()
                    .organizationName(event.get(Const.ORGANIZATION_NAME_NAME))
                    .build();
            CreateOrganizationResponseBody responseBody = organizationService.createOrganization(requestBody);
            return gson.toJson(responseBody);
        }, event);
        return null;
    }
}