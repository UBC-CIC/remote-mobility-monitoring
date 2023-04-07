package com.cpen491.remote_mobility_monitoring.function.handler.organization;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.cpen491.remote_mobility_monitoring.function.handler.HandlerParent;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.CreateOrganizationRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.CreateOrganizationResponseBody;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processGenericRequest;

@Slf4j
public class CreateOrganizationHandler extends HandlerParent implements RequestHandler<Map<String, String>, String> {
    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        log.info("Received Create Organization request with event {}", event);
        return processGenericRequest((request) -> {
            CreateOrganizationRequestBody requestBody = CreateOrganizationRequestBody.builder()
                    .organizationName(event.get(Const.ORGANIZATION_NAME_NAME))
                    .build();
            CreateOrganizationResponseBody responseBody = organizationService.createOrganization(requestBody);
            log.info("Responding to Create Organization request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, event);
    }
}
