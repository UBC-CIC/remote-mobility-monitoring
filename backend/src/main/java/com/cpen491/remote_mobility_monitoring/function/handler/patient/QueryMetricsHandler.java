package com.cpen491.remote_mobility_monitoring.function.handler.patient;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.function.Config;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.QueryMetricsRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.QueryMetricsResponseBody;
import com.cpen491.remote_mobility_monitoring.function.service.PatientService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class QueryMetricsHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final PatientService patientService;
    private final Gson gson;

    public QueryMetricsHandler() {
        Config config = Config.instance();
        this.patientService = config.patientService();
        this.gson = config.gson();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Query Metrics request with multi-value query parameters: {}, and query parameters: {}",
                requestEvent.getMultiValueQueryStringParameters(), requestEvent.getQueryStringParameters());
        return processApiGatewayRequest((request) -> {
            List<String> patientIds = request.getMultiValueQueryStringParameters().get(Const.PATIENTS_NAME);
            String start = request.getQueryStringParameters().get(Const.START_NAME);
            String end = request.getQueryStringParameters().get(Const.END_NAME);
            QueryMetricsRequestBody requestBody = QueryMetricsRequestBody.builder()
                    .patientIds(patientIds)
                    .start(start)
                    .end(end)
                    .build();
            QueryMetricsResponseBody responseBody = patientService.queryMetrics(requestBody);
            log.info("Responding to Query Metrics request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
