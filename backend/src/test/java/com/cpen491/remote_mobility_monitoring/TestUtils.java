package com.cpen491.remote_mobility_monitoring;

import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics;
import com.cpen491.remote_mobility_monitoring.datastore.model.Metrics.MeasureName;
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

    public static Organization buildOrganization(String pid, String sid, String name) {
        return Organization.builder()
                .pid(pid)
                .sid(sid)
                .name(name)
                .build();
    }

    public static Admin buildAdmin(String pid, String sid, String email, String firstName, String lastName) {
        return Admin.builder()
                .pid(pid)
                .sid(sid)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    public static Caregiver buildCaregiver(String pid, String sid, String email, String firstName, String lastName,
                                           String title, String phoneNumber) {
        return Caregiver.builder()
                .pid(pid)
                .sid(sid)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .title(title)
                .phoneNumber(phoneNumber)
                .build();
    }

    public static Patient buildPatient(String pid, String sid, String email, String deviceId, String firstName, String lastName,
                                       String phoneNumber) {
        return Patient.builder()
                .pid(pid)
                .sid(sid)
                .email(email)
                .deviceId(deviceId)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .build();
    }

    public static Metrics buildMetrics(String patientId, MeasureName measureName, String measureValue, String timestamp) {
        return Metrics.builder()
                .patientId(patientId)
                .measureName(measureName)
                .measureValue(measureValue)
                .timestamp(timestamp)
                .build();
    }
}
