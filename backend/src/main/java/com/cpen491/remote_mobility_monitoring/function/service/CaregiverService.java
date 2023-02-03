package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import com.cpen491.remote_mobility_monitoring.function.schema.Const;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.CreateCognitoUserRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.CreateCognitoUserResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AddPatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AddPatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.DeleteCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.DeleteCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetAllPatientsRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetAllPatientsResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetAllPatientsResponseBody.PatientSerialization;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.RemovePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.RemovePatientResponseBody;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class CaregiverService {
    @NonNull
    private CaregiverDao caregiverDao;
    @NonNull
    private CognitoIdentityProviderClient cognitoIdentityProviderClient;
    /**
     * Creates a Caregiver and adds it to an Organization.
     *
     * @param body The request body
     * @return {@link CreateCaregiverResponseBody}
     * @throws RecordDoesNotExistException If Organization record with given organizationId does not exist
     * @throws DuplicateRecordException If record with the given email already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of email, firstName, lastName,
     *                              title, phoneNumber, or organizationId are empty
     */
    public CreateCaregiverResponseBody createCaregiver(CreateCaregiverRequestBody body) {
        Validator.validateCreateCaregiverRequestBody(body);

        UserType cognitoUser;
        AdminCreateUserRequest adminCreateUserParams = AdminCreateUserRequest.builder()
                .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
                //.messageAction(MessageActionType.SUPPRESS)
                .username(body.getEmail())
                .userAttributes(
                        AttributeType.builder().name("email_verified").value("true").build(),
                        AttributeType.builder().name("email").value(body.getEmail()).build(),
                        AttributeType.builder().name("given_name").value(body.getFirstName()).build(),
                        AttributeType.builder().name("family_name").value(body.getLastName()).build(),
                        AttributeType.builder().name("phone_number").value(body.getPhoneNumber()).build()
                )
                .userPoolId(Const.COGNITO_USERPOOL_ID == null ? "us-west-2_killme" : Const.COGNITO_USERPOOL_ID)
                .build();
        try {
            log.info("creating user in cognito");
            cognitoIdentityProviderClient.adminCreateUser(adminCreateUserParams);
            log.info("user created in cognito");
        } catch (CognitoIdentityProviderException e) {
            log.error("error creating user in cognito", e);
            throw e;
        } catch (Exception e) {
            log.error("error creating user in cognito", e);
            throw new RuntimeException(e);
        }

        // Switch to sub if we want to index by userID instead of email
//        String sub;
//        try {
//            sub = cognitoUser.attributes().stream().filter(attributeType -> attributeType.name().equals("sub")).findFirst().get().value();
//        }
//        catch (Exception e) {
//            log.error("error getting sub from cognito", e);
//            throw new RuntimeException(e);
//        }

        Caregiver caregiver = Caregiver.builder()
                .email(body.getEmail())
                .firstName(body.getFirstName())
                .lastName(body.getLastName())
                .title(body.getTitle())
                .phoneNumber(body.getPhoneNumber())
                .build();
        caregiverDao.create(caregiver, body.getOrganizationId());

        return CreateCaregiverResponseBody.builder()
                .message("OK")
                .build();
    }

    /**
     * Adds a Patient to a Caregiver. Patient and Caregiver must already exist.
     *
     * @param body The request body
     * @return {@link AddPatientResponseBody}
     * @throws RecordDoesNotExistException If Patient or Caregiver records do not exist
     * @throws DuplicateRecordException If Patient/Caregiver association already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId or caregiverId is empty or invalid
     */
    public AddPatientResponseBody addPatient(AddPatientRequestBody body) {
        Validator.validateAddPatientRequestBody(body);

        caregiverDao.addPatient(body.getPatientId(), body.getCaregiverId());

        return AddPatientResponseBody.builder()
                .message("OK")
                .build();
    }

    /**
     * Removes a Patient from a Caregiver. Patient and Caregiver must already exist.
     *
     * @param body The request body
     * @return {@link RemovePatientResponseBody}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId or caregiverId is empty or invalid
     */
    public RemovePatientResponseBody removePatient(RemovePatientRequestBody body) {
        Validator.validateRemovePatientRequestBody(body);

        caregiverDao.removePatient(body.getPatientId(), body.getCaregiverId());

        return RemovePatientResponseBody.builder()
                .message("OK")
                .build();
    }

    /**
     * Gets the Caregiver specified by caregiverId.
     *
     * @param body The request body
     * @return {@link GetCaregiverResponseBody}
     * @throws RecordDoesNotExistException If Caregiver record with the given caregiverId does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if caregiverId is empty
     */
    public GetCaregiverResponseBody getCaregiver(GetCaregiverRequestBody body) {
        Validator.validateGetCaregiverRequestBody(body);

        Caregiver caregiver = caregiverDao.findById(body.getCaregiverId());
        Organization organization = caregiverDao.findOrganization(body.getCaregiverId());

        return GetCaregiverResponseBody.builder()
                .email(caregiver.getEmail())
                .firstName(caregiver.getFirstName())
                .lastName(caregiver.getLastName())
                .title(caregiver.getTitle())
                .phoneNumber(caregiver.getPhoneNumber())
                .organizationId(organization.getPid())
                .organizationName(organization.getName())
                .createdAt(caregiver.getCreatedAt())
                .build();
    }

    /**
     * Gets all Patients for a Caregiver.
     *
     * @param body The request body
     * @return {@link GetAllPatientsResponseBody}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if caregiverId is empty
     */
    public GetAllPatientsResponseBody getAllPatients(GetAllPatientsRequestBody body) {
        Validator.validateGetAllPatientsRequestBody(body);

        List<Patient> patients = caregiverDao.findAllPatients(body.getCaregiverId());

        return GetAllPatientsResponseBody.builder()
                .patients(patients.stream().map(PatientSerialization::fromPatient).collect(Collectors.toList()))
                .build();
    }

    /**
     * Deletes a Caregiver.
     *
     * @param body The request body
     * @return {@link DeleteCaregiverResponseBody}
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if caregiverId is empty
     */
    public DeleteCaregiverResponseBody deleteCaregiver(DeleteCaregiverRequestBody body) {
        Validator.validateDeleteCaregiverRequestBody(body);

        caregiverDao.delete(body.getCaregiverId());

        return DeleteCaregiverResponseBody.builder()
                .message("OK")
                .build();
    }


    public CreateCognitoUserResponseBody createCognitoUser(CreateCognitoUserRequestBody body) {
        Validator.validateCreateAdminUserRequestBody(body);

        AdminCreateUserResponse cognitoUser;
        AdminCreateUserRequest adminCreateUserParams = AdminCreateUserRequest.builder()
                .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
                //.messageAction(MessageActionType.SUPPRESS)
                .username(body.getEmail())
                .userAttributes(
                        AttributeType.builder().name("email_verified").value("true").build(),
                        AttributeType.builder().name("email").value(body.getEmail()).build(),
                        AttributeType.builder().name("given_name").value(body.getFirstName()).build(),
                        AttributeType.builder().name("family_name").value(body.getLastName()).build(),
                        AttributeType.builder().name("phone_number").value(body.getPhoneNumber()).build()
                )
                .userPoolId(Const.COGNITO_USERPOOL_ID)
                .build();
        try {
            log.info("creating user in cognito");
            cognitoUser = cognitoIdentityProviderClient.adminCreateUser(adminCreateUserParams);
            log.info("user created in cognito");
        } catch (Exception e) {
            log.error("error creating user in cognito", e);
            throw new RuntimeException(e);
        }

        return CreateCognitoUserResponseBody.builder()
                .message("OK")
                .build();
    }
}
