package com.cpen491.remote_mobility_monitoring.function.schema.organization;

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
public class CreateOrganizationRequestBody {
    @SerializedName(Const.ORGANIZATION_NAME_NAME)
    private String organizationName;
}
