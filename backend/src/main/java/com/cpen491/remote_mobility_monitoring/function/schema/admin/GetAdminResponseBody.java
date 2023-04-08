package com.cpen491.remote_mobility_monitoring.function.schema.admin;

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
public class GetAdminResponseBody {
    @SerializedName(Const.EMAIL_NAME)
    private String email;
    @SerializedName(Const.FIRST_NAME_NAME)
    private String firstName;
    @SerializedName(Const.LAST_NAME_NAME)
    private String lastName;
    @SerializedName(Const.ORGANIZATION_ID_NAME)
    private String organizationId;
    @SerializedName(Const.ORGANIZATION_NAME_NAME)
    private String organizationName;
    @SerializedName(Const.CREATED_AT_NAME)
    private String createdAt;
}
