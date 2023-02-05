package com.cpen491.remote_mobility_monitoring.function.schema.organization;

import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
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
public class GetOrganizationResponseBody {
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

        public static CaregiverSerialization fromCaregiver(Caregiver caregiver) {
            return CaregiverSerialization.builder()
                    .caregiverId(caregiver.getPid())
                    .firstName(caregiver.getFirstName())
                    .lastName(caregiver.getLastName())
                    .build();
        }
    }

    @Getter
    @Builder
    @ToString
    @EqualsAndHashCode
    public static class AdminSerialization {
        @SerializedName(Const.ADMIN_ID_NAME)
        private String adminId;
        @SerializedName(Const.FIRST_NAME_NAME)
        private String firstName;
        @SerializedName(Const.LAST_NAME_NAME)
        private String lastName;

        public static AdminSerialization fromAdmin(Admin admin) {
            return AdminSerialization.builder()
                    .adminId(admin.getPid())
                    .firstName(admin.getFirstName())
                    .lastName(admin.getLastName())
                    .build();
        }
    }

    @SerializedName(Const.ORGANIZATION_NAME_NAME)
    private String organizationName;

    @SerializedName(Const.ADMINS_NAME)
    private List<AdminSerialization> admins;

    @SerializedName(Const.CAREGIVERS_NAME)
    private List<CaregiverSerialization> caregivers;
}
