package com.cpen491.remote_mobility_monitoring.function.handler.admin;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.GetAdminRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.GetAdminResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.AdminService;
import com.google.gson.Gson;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.process;

public class GetAdminHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final AdminService adminService;
    private final Gson gson;

    public GetAdminHandler() {
        Config config = Config.instance();
        this.adminService = config.adminService();
        this.gson = config.gson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        return process((request) -> {
            String adminId = request.getPathParameters().get(Const.ADMIN_ID_NAME);
            GetAdminRequestBody requestBody = GetAdminRequestBody.builder()
                    .adminId(adminId)
                    .build();
            GetAdminResponseBody responseBody = adminService.getAdmin(requestBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
