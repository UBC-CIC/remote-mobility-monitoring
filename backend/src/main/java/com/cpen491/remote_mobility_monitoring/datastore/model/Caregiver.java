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

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.CaregiverTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.getBoolFromMap;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.getFromMap;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.putInMap;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Caregiver extends BaseModel {
    private String email;
    private String firstName;
    private String lastName;
    private String title;
    private String phoneNumber;
    // The following 3 attributes are only required in records that associate a patient with his/her primary caregiver
    private String authCode;
    private String authCodeTimestamp;
    private Boolean isPrimary;

    public static Map<String, AttributeValue> convertToMap(Caregiver caregiver) {
        Map<String, AttributeValue> map = new HashMap<>();
        putInMap(map, CaregiverTable.PID_NAME, caregiver.getPid());
        putInMap(map, CaregiverTable.SID_NAME, caregiver.getSid());
        putInMap(map, CaregiverTable.EMAIL_NAME, caregiver.getEmail());
        putInMap(map, CaregiverTable.FIRST_NAME_NAME, caregiver.getFirstName());
        putInMap(map, CaregiverTable.LAST_NAME_NAME, caregiver.getLastName());
        putInMap(map, CaregiverTable.TITLE_NAME, caregiver.getTitle());
        putInMap(map, CaregiverTable.PHONE_NUMBER_NAME, caregiver.getPhoneNumber());
        putInMap(map, CaregiverTable.CREATED_AT_NAME, caregiver.getCreatedAt());
        putInMap(map, CaregiverTable.UPDATED_AT_NAME, caregiver.getUpdatedAt());
        return map;
    }

    public static Map<String, AttributeValue> convertPrimaryToMap(Caregiver caregiver) {
        Map<String, AttributeValue> map = convertToMap(caregiver);
        putInMap(map, CaregiverTable.AUTH_CODE_NAME, caregiver.getAuthCode());
        putInMap(map, CaregiverTable.AUTH_CODE_TIMESTAMP_NAME, caregiver.getAuthCodeTimestamp());
        putInMap(map, CaregiverTable.IS_PRIMARY_NAME, true);
        return map;
    }

    public static Caregiver convertFromMap(Map<String, AttributeValue> map) {
        return Caregiver.builder()
                .pid(getFromMap(map, CaregiverTable.PID_NAME))
                .sid(getFromMap(map, CaregiverTable.SID_NAME))
                .email(getFromMap(map, CaregiverTable.EMAIL_NAME))
                .firstName(getFromMap(map, CaregiverTable.FIRST_NAME_NAME))
                .lastName(getFromMap(map, CaregiverTable.LAST_NAME_NAME))
                .title(getFromMap(map, CaregiverTable.TITLE_NAME))
                .phoneNumber(getFromMap(map, CaregiverTable.PHONE_NUMBER_NAME))
                .createdAt(getFromMap(map, CaregiverTable.CREATED_AT_NAME))
                .updatedAt(getFromMap(map, CaregiverTable.UPDATED_AT_NAME))
                .build();
    }

    public static Caregiver convertUnverifiedPrimaryFromMap(Map<String, AttributeValue> map) {
        Caregiver caregiver = convertFromMap(map);
        caregiver.setAuthCode(getFromMap(map, CaregiverTable.AUTH_CODE_NAME));
        caregiver.setAuthCodeTimestamp(getFromMap(map, CaregiverTable.AUTH_CODE_TIMESTAMP_NAME));
        return caregiver;
    }

    public static Caregiver convertPrimaryFromMap(Map<String, AttributeValue> map) {
        Caregiver caregiver = convertFromMap(map);
        caregiver.setIsPrimary(getBoolFromMap(map, CaregiverTable.IS_PRIMARY_NAME));
        return caregiver;
    }
}
