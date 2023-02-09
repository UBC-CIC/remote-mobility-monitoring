package com.cpen491.remote_mobility_monitoring.function.handler.patient;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.AddMetricsRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.AddMetricsResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class AddMetricsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final PatientService patientService;
    private final Gson gson;

    public AddMetricsHandler() {
        Config config = Config.instance();
        this.patientService = config.patientService();
        this.gson = config.gson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Add Metrics request with body: {}", requestEvent.getBody());
        return processApiGatewayRequest((request) -> {
            AddMetricsRequestBody requestBody = gson.fromJson(request.getBody(), AddMetricsRequestBody.class);
            AddMetricsResponseBody responseBody = patientService.addMetrics(requestBody);
            log.info("Responding to Add Metrics request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
