package com.cpen491.remote_mobility_monitoring.function.handler.admin;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.handler.HandlerParent;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.DeleteAdminRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.DeleteAdminResponseBody;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class DeleteAdminHandler extends HandlerParent implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Delete Admin request with path parameters: {}", requestEvent.getPathParameters());
        return processApiGatewayRequest((request) -> {
            String adminId = request.getPathParameters().get(Const.ADMIN_ID_NAME);
            DeleteAdminRequestBody requestBody = DeleteAdminRequestBody.builder()
                    .adminId(adminId)
                    .build();
            DeleteAdminResponseBody responseBody = adminService.deleteAdmin(requestBody);
            log.info("Responding to Delete Admin request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
