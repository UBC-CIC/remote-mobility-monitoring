package com.cpen491.remote_mobility_monitoring.function.schema.patient;

import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPatientResponseBody {
    @SerializedName(Const.EMAIL_NAME)
    private String email;
    @SerializedName(Const.FIRST_NAME_NAME)
    private String firstName;
    @SerializedName(Const.LAST_NAME_NAME)
    private String lastName;
    @SerializedName(Const.PHONE_NUMBER_NAME)
    private String phoneNumber;
    @SerializedName(Const.PATIENT_BIRTHDAY)
    private String birthday;
    @SerializedName(Const.PATIENT_SEX)
    private String sex;
    @SerializedName(Const.PATIENT_HEIGHT)
    private Float height;
    @SerializedName(Const.PATIENT_WEIGHT)
    private Float weight;
    @SerializedName(Const.CREATED_AT_NAME)
    private String createdAt;
}
