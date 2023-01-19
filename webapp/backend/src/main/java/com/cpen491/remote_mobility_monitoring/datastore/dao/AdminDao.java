package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
public class AdminDao {
    @NonNull
    DynamoDbTable<Admin> table;

    public void create(Admin newRecord) {
        log.info("Creating new Admin record {}", newRecord);

        newRecord.setId(UUID.randomUUID().toString());

        String currentTime = LocalDateTime.now().toString();
        newRecord.setCreatedAt(currentTime);
        newRecord.setUpdatedAt(currentTime);

        try {
            table.putItem(newRecord);
        } catch (ConditionalCheckFailedException e) {
            e.printStackTrace();
            // Do nothing for idempotence
        }
    }

    public Admin find(String id) {
        log.info("Finding Admin record with id [{}]", id);

        Admin toFind = Admin.builder().id(id).build();
        Admin found = table.getItem(toFind);
        if (found == null) {
            log.error("Cannot find Admin record with id [{}]", id);
            throw new RuntimeException("Cannot find Admin record");
        } else {
            return found;
        }
    }
}
