package com.cpen491.remote_mobility_monitoring.function.schema.patient;

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
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("date_of_birth")
    private String dateOfBirth;
}
