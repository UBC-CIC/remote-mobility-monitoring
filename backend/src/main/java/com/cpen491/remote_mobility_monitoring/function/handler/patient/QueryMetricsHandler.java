package com.cpen491.remote_mobility_monitoring.function.handler.patient;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cpen491.remote_mobility_monitoring.dependency.utility.JwtUtils;
import com.cpen491.remote_mobility_monitoring.function.handler.HandlerParent;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.QueryMetricsRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.QueryMetricsResponseBody;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.processApiGatewayRequest;

@Slf4j
public class QueryMetricsHandler extends HandlerParent implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        log.info("Received Query Metrics request with multi-value query parameters: {}, and query parameters: {}",
                requestEvent.getMultiValueQueryStringParameters(), requestEvent.getQueryStringParameters());
        return processApiGatewayRequest((request) -> {
            String rawId = JwtUtils.getIdFromHeader(request.getHeaders(), gson);
            List<String> patientIds = request.getMultiValueQueryStringParameters().get(Const.PATIENTS_NAME);
            for (String patientId : patientIds) {
                authService.caregiverHasPatient(rawId, patientId);
            }
            Map<String, String> queryParameters = request.getQueryStringParameters();
            QueryMetricsRequestBody requestBody = QueryMetricsRequestBody.builder()
                    .patientIds(patientIds)
                    .start(queryParameters.get(Const.START_NAME))
                    .end(queryParameters.get(Const.END_NAME))
                    .minAge(queryParameters.get(Const.MIN_AGE) == null ? null : Integer.parseInt(queryParameters.get(Const.MIN_AGE)))
                    .maxAge(queryParameters.get(Const.MAX_AGE) == null ? null : Integer.parseInt(queryParameters.get(Const.MAX_AGE)))
                    .maxHeight(queryParameters.get(Const.MAX_HEIGHT) == null ? null : Float.parseFloat(queryParameters.get(Const.MAX_HEIGHT)))
                    .minHeight(queryParameters.get(Const.MIN_HEIGHT) == null ? null : Float.parseFloat(queryParameters.get(Const.MIN_HEIGHT)))
                    .maxWeight(queryParameters.get(Const.MAX_WEIGHT) == null ? null : Float.parseFloat(queryParameters.get(Const.MAX_WEIGHT)))
                    .minWeight(queryParameters.get(Const.MIN_WEIGHT) == null ? null : Float.parseFloat(queryParameters.get(Const.MIN_WEIGHT)))
                    .sex(queryParameters.get(Const.SEX))
                    .build();
            QueryMetricsResponseBody responseBody = patientService.queryMetrics(requestBody);
            log.info("Responding to Query Metrics request with response body {}", responseBody);
            return gson.toJson(responseBody);
        }, requestEvent);
    }
}
