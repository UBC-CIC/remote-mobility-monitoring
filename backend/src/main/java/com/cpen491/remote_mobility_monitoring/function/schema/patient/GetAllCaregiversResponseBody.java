package com.cpen491.remote_mobility_monitoring.function.schema.patient;

import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllCaregiversResponseBody {
    @Builder
    @ToString
    @EqualsAndHashCode
    public static class CaregiverSerialization {
        @SerializedName(Const.CAREGIVER_ID_NAME)
        private String caregiverId;
        @SerializedName(Const.FIRST_NAME_NAME)
        private String firstName;
        @SerializedName(Const.LAST_NAME_NAME)
        private String lastName;

        public static CaregiverSerialization fromCaregiver(Caregiver caregiver) {
            return CaregiverSerialization.builder()
                    .caregiverId(caregiver.getPid())
                    .firstName(caregiver.getFirstName())
                    .lastName(caregiver.getLastName())
                    .build();
        }
    }

    @SerializedName(Const.CAREGIVERS_NAME)
    private List<CaregiverSerialization> caregivers;
}
