package com.cpen491.remote_mobility_monitoring.datastore.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.PatientTable;

@DynamoDbBean
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Patient extends BaseModel {
    private String deviceId;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String phoneNumber;
    private String authCode;
    private String authCodeTimestamp;
    private Boolean verified;

    @DynamoDbPartitionKey
    @DynamoDbAttribute(PatientTable.ID_NAME)
    public String getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {PatientTable.DEVICE_ID_INDEX_NAME})
    @DynamoDbAttribute(PatientTable.DEVICE_ID_NAME)
    public String getDeviceId() {
        return deviceId;
    }

    @DynamoDbAttribute(PatientTable.FIRST_NAME_NAME)
    public String getFirstName() {
        return firstName;
    }

    @DynamoDbAttribute(PatientTable.LAST_NAME_NAME)
    public String getLastName() {
        return lastName;
    }

    @DynamoDbAttribute(PatientTable.DATE_OF_BIRTH_NAME)
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    @DynamoDbAttribute(PatientTable.PHONE_NUMBER_NAME)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @DynamoDbAttribute(PatientTable.AUTH_CODE_NAME)
    public String getAuthCode() {
        return authCode;
    }

    @DynamoDbAttribute(PatientTable.AUTH_CODE_TIMESTAMP_NAME)
    public String getAuthCodeTimestamp() {
        return authCodeTimestamp;
    }

    @DynamoDbAttribute(PatientTable.VERIFIED_NAME)
    public Boolean getVerified() {
        return verified;
    }

    @DynamoDbAttribute(PatientTable.CREATED_AT_NAME)
    public String getCreatedAt() {
        return createdAt;
    }

    @DynamoDbAttribute(PatientTable.UPDATED_AT_NAME)
    public String getUpdatedAt() {
        return updatedAt;
    }
}
