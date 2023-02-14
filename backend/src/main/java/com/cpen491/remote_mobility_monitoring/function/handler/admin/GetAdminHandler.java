package com.cpen491.remote_mobility_monitoring.function.handler.admin;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.handler.HandlerParent;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.GetAdminRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.GetAdminResponseBody;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class GetAdminHandler extends HandlerParent implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Get Admin request with path parameters: {}", requestEvent.getPathParameters());
        return processApiGatewayRequest((request) -> {
            String adminId = request.getPathParameters().get(Const.ADMIN_ID_NAME);
            GetAdminRequestBody requestBody = GetAdminRequestBody.builder()
                    .adminId(adminId)
                    .build();
            GetAdminResponseBody responseBody = adminService.getAdmin(requestBody);
            log.info("Responding to Get Admin request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
