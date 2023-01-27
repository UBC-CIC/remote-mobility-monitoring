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

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.OrganizationTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.getFromMap;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.putInMap;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends BaseModel {
    private String name;

    public static Map<String, AttributeValue> convertToMap(Organization organization) {
        Map<String, AttributeValue> map = new HashMap<>();
        putInMap(map, OrganizationTable.PID_NAME, organization.getPid());
        putInMap(map, OrganizationTable.SID_NAME, organization.getSid());
        putInMap(map, OrganizationTable.NAME_NAME, organization.getName());
        putInMap(map, OrganizationTable.CREATED_AT_NAME, organization.getCreatedAt());
        putInMap(map, OrganizationTable.UPDATED_AT_NAME, organization.getUpdatedAt());
        return map;
    }

    public static Organization convertFromMap(Map<String, AttributeValue> map) {
        return Organization.builder()
                .pid(getFromMap(map, OrganizationTable.PID_NAME))
                .sid(getFromMap(map, OrganizationTable.SID_NAME))
                .name(getFromMap(map, OrganizationTable.NAME_NAME))
                .createdAt(getFromMap(map, OrganizationTable.CREATED_AT_NAME))
                .updatedAt(getFromMap(map, OrganizationTable.UPDATED_AT_NAME))
                .build();
    }
}
