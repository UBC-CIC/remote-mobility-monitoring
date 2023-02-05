package com.cpen491.remote_mobility_monitoring.function.handler.admin;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.CreateAdminRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.CreateAdminResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.AdminService;
import com.google.gson.Gson;

import java.util.Map;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processGenericRequest;

public class CreateAdminHandler implements RequestHandler<Map<String, String>, Void> {
    private final AdminService adminService;
    private final Gson gson;

    public CreateAdminHandler() {
        Config config = Config.instance();
        this.adminService = config.adminService();
        this.gson = config.gson();
    }

    @Override
    public Void handleRequest(Map<String, String> event, Context context) {
        processGenericRequest((request) -> {
            CreateAdminRequestBody requestBody = CreateAdminRequestBody.builder()
                    .email(event.get(Const.EMAIL_NAME))
                    .firstName(event.get(Const.FIRST_NAME_NAME))
                    .lastName(event.get(Const.LAST_NAME_NAME))
                    .organizationId(event.get(Const.ORGANIZATION_ID_NAME))
                    .build();
            CreateAdminResponseBody responseBody = adminService.createAdmin(requestBody);
            return gson.toJson(responseBody);
        }, event);
        return null;
    }
}
