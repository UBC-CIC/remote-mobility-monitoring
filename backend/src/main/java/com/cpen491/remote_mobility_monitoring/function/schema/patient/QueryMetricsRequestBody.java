package com.cpen491.remote_mobility_monitoring.function.schema.patient;

import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryMetricsRequestBody {
    @SerializedName(Const.PATIENTS_NAME)
    private List<String> patientIds;
    @SerializedName(Const.START_NAME)
    private String start;
    @SerializedName(Const.END_NAME)
    private String end;
    @SerializedName(Const.SEX)
    private String sex;
    @SerializedName(Const.MIN_AGE)
    private Integer minAge;
    @SerializedName(Const.MAX_AGE)
    private Integer maxAge;
    @SerializedName(Const.MIN_HEIGHT)
    private Float minHeight;
    @SerializedName(Const.MAX_HEIGHT)
    private Float maxHeight;
    @SerializedName(Const.MIN_WEIGHT)
    private Float minWeight;
    @SerializedName(Const.MAX_WEIGHT)
    private Float maxWeight;
}
