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
public class CreatePatientRequestBody {
    @SerializedName(Const.EMAIL_NAME)
    private String email;
    @SerializedName(Const.PASSWORD_NAME)
    private String password;
    @SerializedName(Const.FIRST_NAME_NAME)
    private String firstName;
    @SerializedName(Const.LAST_NAME_NAME)
    private String lastName;
    @SerializedName(Const.PHONE_NUMBER_NAME)
    private String phoneNumber;
}
