package com.cpen491.remote_mobility_monitoring.function.schema.caregiver;

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
public class CreateCaregiverRequestBody {
    @SerializedName(Const.EMAIL_NAME)
    private String email;
    @SerializedName(Const.FIRST_NAME_NAME)
    private String firstName;
    @SerializedName(Const.LAST_NAME_NAME)
    private String lastName;
    @SerializedName(Const.TITLE_NAME)
    private String title;
    @SerializedName(Const.PHONE_NUMBER_NAME)
    private String phoneNumber;
    @SerializedName(Const.ORGANIZATION_ID_NAME)
    private String organizationId;
    @SerializedName(Const.PASSWORD_NAME)
    private String password;
    @SerializedName(Const.SUPPRESS_EMAIL_NAME)
    private boolean suppressEmail;
    @SerializedName(Const.SKIP_PASSWORD_CHANGE_NAME)
    private boolean skipPasswordChange;

}
