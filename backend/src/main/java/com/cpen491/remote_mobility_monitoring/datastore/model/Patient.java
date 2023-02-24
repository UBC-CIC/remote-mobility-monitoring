package com.cpen491.remote_mobility_monitoring.datastore.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.PatientTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.getFromMap;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.putInMap;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends BaseModel {
    private String email;
    private String deviceId;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public static Map<String, AttributeValue> convertToMap(Patient patient) {
        Map<String, AttributeValue> map = new HashMap<>();
        putInMap(map, PatientTable.PID_NAME, patient.getPid());
        putInMap(map, PatientTable.SID_NAME, patient.getSid());
        putInMap(map, PatientTable.EMAIL_NAME, patient.getEmail());
        putInMap(map, PatientTable.DEVICE_ID_NAME, patient.getDeviceId());
        putInMap(map, PatientTable.FIRST_NAME_NAME, patient.getFirstName());
        putInMap(map, PatientTable.LAST_NAME_NAME, patient.getLastName());
        putInMap(map, PatientTable.PHONE_NUMBER_NAME, patient.getPhoneNumber());
        putInMap(map, PatientTable.CREATED_AT_NAME, patient.getCreatedAt());
        putInMap(map, PatientTable.UPDATED_AT_NAME, patient.getUpdatedAt());
        return map;
    }

    public static Patient convertFromMap(Map<String, AttributeValue> map) {
        return Patient.builder()
                .pid(getFromMap(map, PatientTable.PID_NAME))
                .sid(getFromMap(map, PatientTable.SID_NAME))
                .email(getFromMap(map, PatientTable.EMAIL_NAME))
                .deviceId(getFromMap(map, PatientTable.DEVICE_ID_NAME))
                .firstName(getFromMap(map, PatientTable.FIRST_NAME_NAME))
                .lastName(getFromMap(map, PatientTable.LAST_NAME_NAME))
                .phoneNumber(getFromMap(map, PatientTable.PHONE_NUMBER_NAME))
                .createdAt(getFromMap(map, PatientTable.CREATED_AT_NAME))
                .updatedAt(getFromMap(map, PatientTable.UPDATED_AT_NAME))
                .build();
    }
}
