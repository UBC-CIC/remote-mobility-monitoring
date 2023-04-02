package com.cpen491.remote_mobility_monitoring.function.schema.patient;

import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllCaregiversResponseBody {
    @Getter
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
        @SerializedName(Const.IS_PRIMARY_NAME)
        private Boolean isPrimary;
        @SerializedName(Const.VERIFIED_NAME)
        private Boolean verified;

        public static CaregiverSerialization fromCaregiver(Caregiver caregiver) {
            return CaregiverSerialization.builder()
                    .caregiverId(caregiver.getPid())
                    .firstName(caregiver.getFirstName())
                    .lastName(caregiver.getLastName())
                    .isPrimary(caregiver.getIsPrimary())
                    .verified(caregiver.getVerified())
                    .build();
        }
    }

    @SerializedName(Const.CAREGIVERS_NAME)
    private List<CaregiverSerialization> caregivers;
}
