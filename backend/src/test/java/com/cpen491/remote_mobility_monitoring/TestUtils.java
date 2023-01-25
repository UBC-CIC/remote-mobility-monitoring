package com.cpen491.remote_mobility_monitoring;

import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import org.assertj.core.api.ThrowableAssert;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class TestUtils {
    public static void assertInvalidInputExceptionThrown(ThrowableAssert.ThrowingCallable shouldRaiseThrowable, String errorMessage) {
        assertThatThrownBy(shouldRaiseThrowable)
                .isInstanceOfAny(IllegalArgumentException.class, NullPointerException.class)
                .hasMessage(errorMessage);
    }

    public static Organization buildOrganization(String id, String name) {
        return Organization.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static Admin buildAdmin(String id, String email, String firstName, String lastName, String organizationId) {
        return Admin.builder()
                .id(id)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .organizationId(organizationId)
                .build();
    }

    public static Caregiver buildCaregiver(String id, String email, String firstName, String lastName,
                                           String title, String phoneNumber, String imageUrl, String organizationId) {
        return Caregiver.builder()
                .id(id)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .title(title)
                .phoneNumber(phoneNumber)
                .imageUrl(imageUrl)
                .organizationId(organizationId)
                .build();
    }

    public static Patient buildPatient(String id, String deviceId, String firstName, String lastName,
                                        String dateOfBirth, String phoneNumber) {
        return Patient.builder()
                .id(id)
                .deviceId(deviceId)
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(dateOfBirth)
                .phoneNumber(phoneNumber)
                .build();
    }
}
