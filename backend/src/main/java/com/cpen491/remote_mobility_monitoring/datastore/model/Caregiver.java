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

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.CaregiverTable;

@DynamoDbBean
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
    private String imageUrl;
    private String organizationId;

    @DynamoDbPartitionKey
    @DynamoDbAttribute(CaregiverTable.ID_NAME)
    public String getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {CaregiverTable.EMAIL_INDEX_NAME})
    @DynamoDbAttribute(CaregiverTable.EMAIL_NAME)
    public String getEmail() {
        return email;
    }

    @DynamoDbAttribute(CaregiverTable.FIRST_NAME_NAME)
    public String getFirstName() {
        return firstName;
    }

    @DynamoDbAttribute(CaregiverTable.LAST_NAME_NAME)
    public String getLastName() {
        return lastName;
    }

    @DynamoDbAttribute(CaregiverTable.TITLE_NAME)
    public String getTitle() {
        return title;
    }

    @DynamoDbAttribute(CaregiverTable.PHONE_NUMBER_NAME)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @DynamoDbAttribute(CaregiverTable.IMAGE_URL_NAME)
    public String getImageUrl() {
        return imageUrl;
    }

    @DynamoDbAttribute(CaregiverTable.ORGANIZATION_ID_NAME)
    public String getOrganizationId() {
        return organizationId;
    }

    @DynamoDbAttribute(CaregiverTable.CREATED_AT_NAME)
    public String getCreatedAt() {
        return createdAt;
    }

    @DynamoDbAttribute(CaregiverTable.UPDATED_AT_NAME)
    public String getUpdatedAt() {
        return updatedAt;
    }
}
