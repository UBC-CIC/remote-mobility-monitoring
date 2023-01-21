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

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.OrganizationTable;

@DynamoDbBean
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends BaseModel {
    private String name;

    @DynamoDbPartitionKey
    @DynamoDbAttribute(OrganizationTable.ID_NAME)
    public String getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = {OrganizationTable.NAME_INDEX_NAME})
    @DynamoDbAttribute(OrganizationTable.NAME_NAME)
    public String getName() {
        return name;
    }

    @DynamoDbAttribute(OrganizationTable.CREATED_AT_NAME)
    public String getCreatedAt() {
        return createdAt;
    }

    @DynamoDbAttribute(OrganizationTable.UPDATED_AT_NAME)
    public String getUpdatedAt() {
        return updatedAt;
    }
}
