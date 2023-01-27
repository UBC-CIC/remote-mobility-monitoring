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

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.getFromMap;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.DynamoDbUtils.putInMap;

@Getter
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

    public static Map<String, AttributeValue> convertToMap(Admin admin) {
        Map<String, AttributeValue> map = new HashMap<>();
        putInMap(map, AdminTable.PID_NAME, admin.getPid());
        putInMap(map, AdminTable.SID_NAME, admin.getSid());
        putInMap(map, AdminTable.EMAIL_NAME, admin.getEmail());
        putInMap(map, AdminTable.FIRST_NAME_NAME, admin.getFirstName());
        putInMap(map, AdminTable.LAST_NAME_NAME, admin.getLastName());
        putInMap(map, AdminTable.CREATED_AT_NAME, admin.getCreatedAt());
        putInMap(map, AdminTable.UPDATED_AT_NAME, admin.getUpdatedAt());
        return map;
    }

    public static Admin convertFromMap(Map<String, AttributeValue> map) {
        return Admin.builder()
                .pid(getFromMap(map, AdminTable.PID_NAME))
                .sid(getFromMap(map, AdminTable.SID_NAME))
                .email(getFromMap(map, AdminTable.EMAIL_NAME))
                .firstName(getFromMap(map, AdminTable.FIRST_NAME_NAME))
                .lastName(getFromMap(map, AdminTable.LAST_NAME_NAME))
                .createdAt(getFromMap(map, AdminTable.CREATED_AT_NAME))
                .updatedAt(getFromMap(map, AdminTable.UPDATED_AT_NAME))
                .build();
    }
}
