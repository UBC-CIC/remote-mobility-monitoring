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
public class GetAdminRequestBody {
    @SerializedName(Const.ADMIN_ID_NAME)
    private String adminId;
}
