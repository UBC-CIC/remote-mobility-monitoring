package com.cpen491.remote_mobility_monitoring.function.schema.caregiver;

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
    @SerializedName("email")
    private String email;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("title")
    private String title;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("organization_id")
    private String organizationId;
}
