package com.cpen491.remote_mobility_monitoring.function.schema.caregiver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCaregiverResponseBody {
    private String message;
}
