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

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;

@DynamoDbBean
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends BaseModel {
    private String email;
    private String firstName;
    private String lastName;
    private String organizationId;

    @DynamoDbPartitionKey
    @DynamoDbAttribute(AdminTable.ID_NAME)
    public String getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {AdminTable.EMAIL_INDEX_NAME})
    @DynamoDbAttribute(AdminTable.EMAIL_NAME)
    public String getEmail() {
        return email;
    }

    @DynamoDbAttribute(AdminTable.FIRST_NAME_NAME)
    public String getFirstName() {
        return firstName;
    }

    @DynamoDbAttribute(AdminTable.LAST_NAME_NAME)
    public String getLastName() {
        return lastName;
    }

    @DynamoDbAttribute(AdminTable.ORGANIZATION_ID_NAME)
    public String getOrganizationId() {
        return organizationId;
    }

    @DynamoDbAttribute(AdminTable.CREATED_AT_NAME)
    public String getCreatedAt() {
        return createdAt;
    }

    @DynamoDbAttribute(AdminTable.UPDATED_AT_NAME)
    public String getUpdatedAt() {
        return updatedAt;
    }
}
