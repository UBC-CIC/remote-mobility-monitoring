package com.cpen491.remote_mobility_monitoring.datastore.dao;

import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.LocalDateTime;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.AdminTable;

@Slf4j
@AllArgsConstructor
public class AdminDao {
    @NonNull
    DynamoDbTable<Admin> table;

    /**
     * Creates a new Admin record. Record with the same email must not already exist.
     *
     * @param newRecord The Admin record to create
     * @throws DuplicateRecordException If record with the same email already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of email, firstName, lastName, or organizationId are empty
     */
    public void create(Admin newRecord) {
        log.info("Creating new Admin record {}", newRecord);
        Validator.validateAdmin(newRecord);

        // TODO: ensure that organization actually exists, and write tests for it

        String currentTime = LocalDateTime.now().toString();
        newRecord.setCreatedAt(currentTime);
        newRecord.setUpdatedAt(currentTime);

        Expression expression = Expression.builder()
                .expression(String.format("attribute_not_exists(%s)", AdminTable.EMAIL_NAME))
                .build();

        PutItemEnhancedRequest<Admin> request = PutItemEnhancedRequest.builder(Admin.class)
                .conditionExpression(expression)
                .item(newRecord)
                .build();

        try {
            table.putItem(request);
        } catch (ConditionalCheckFailedException e) {
            throw new DuplicateRecordException(Admin.class.getSimpleName(), newRecord.getEmail());
        }
    }

    /**
     * Finds an Admin record by email. Returns null if record does not exist.
     *
     * @param email The email of the record to find
     * @return {@link Admin}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if email is empty
     */
    public Admin find(String email) {
        log.info("Finding Admin record with email [{}]", email);
        Validator.validateEmail(email);

        Admin toFind = Admin.builder().email(email).build();
        Admin found = table.getItem(toFind);
        if (found == null) {
            log.info("Cannot find Admin record with email [{}]", email);
        }

        return found;
    }

    /**
     * Deletes an Admin record by email. Does nothing if record does not exist.
     *
     * @param email The email of the record to delete
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if email is empty
     */
    public void delete(String email) {
        log.info("Deleting Admin record with email [{}]", email);
        Validator.validateEmail(email);

        Admin toDelete = Admin.builder().email(email).build();
        table.deleteItem(toDelete);
    }
}
