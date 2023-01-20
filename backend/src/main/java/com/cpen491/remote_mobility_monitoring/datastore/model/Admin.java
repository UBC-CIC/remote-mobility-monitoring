package com.cpen491.remote_mobility_monitoring.datastore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.extensions.annotations.DynamoDbVersionAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;

@DynamoDbBean
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String organizationId;
    private String createdAt;
    private String updatedAt;
    private Long version;

    @DynamoDbPartitionKey
    @DynamoDbAttribute(AdminTable.ID_NAME)
    public String getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {AdminTable.EMAIL_GSI_NAME})
    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    @DynamoDbAttribute("first_name")
    public String getFirstName() {
        return firstName;
    }

    @DynamoDbAttribute("last_name")
    public String getLastName() {
        return lastName;
    }

    @DynamoDbAttribute("organization_id")
    public String getOrganizationId() {
        return organizationId;
    }

    @DynamoDbAttribute("created_at")
    public String getCreatedAt() {
        return createdAt;
    }

    @DynamoDbAttribute("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }

    @DynamoDbVersionAttribute
    @DynamoDbAttribute("version")
    public Long getVersion() {
        return version;
    }
}
