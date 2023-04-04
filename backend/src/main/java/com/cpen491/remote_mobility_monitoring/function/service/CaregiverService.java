package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.CaregiverDao;
import com.cpen491.remote_mobility_monitoring.datastore.dao.OrganizationDao;
import com.cpen491.remote_mobility_monitoring.datastore.exception.DuplicateRecordException;
import com.cpen491.remote_mobility_monitoring.datastore.exception.RecordDoesNotExistException;
import com.cpen491.remote_mobility_monitoring.datastore.model.Caregiver;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.datastore.model.Patient;
import com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper;
import com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper.CognitoUser;
import com.cpen491.remote_mobility_monitoring.dependency.email.SesWrapper;
import com.cpen491.remote_mobility_monitoring.dependency.exception.CognitoException;
import com.cpen491.remote_mobility_monitoring.dependency.exception.InvalidAuthCodeException;
import com.cpen491.remote_mobility_monitoring.dependency.utility.Validator;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AcceptPatientPrimaryRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AcceptPatientPrimaryResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AddPatientPrimaryRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.AddPatientPrimaryResponseBody;
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
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.UpdateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.UpdateCaregiverResponseBody;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.cpen491.remote_mobility_monitoring.datastore.model.Const.CaregiverTable;
import static com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper.CAREGIVER_GROUP_NAME;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTime;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.parseTime;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.secondsBetweenTimes;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@RequiredArgsConstructor
public class CaregiverService {
    private static final long TTL = 172_800;

    @NonNull
    private CaregiverDao caregiverDao;
    @NonNull
    private OrganizationDao organizationDao;
    @NonNull
    private CognitoWrapper cognitoWrapper;
    @NonNull
    private SesWrapper sesWrapper;

    /**
     * Creates a Caregiver in database and Cognito and adds it to an Organization.
     *
     * @param body The request body
     * @return {@link CreateCaregiverResponseBody}
     * @throws CognitoException If Cognito fails to create user or add user to group
     * @throws RecordDoesNotExistException If Organization record with given organizationId does not exist
     * @throws DuplicateRecordException If record with the given email already exists
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of email, firstName, lastName,
     *                              title, phoneNumber, or organizationId are empty
     */
    public CreateCaregiverResponseBody createCaregiver(CreateCaregiverRequestBody body) {
        log.info("Creating Caregiver {}", body);
        Validator.validateCreateCaregiverRequestBody(body);

        organizationDao.findById(body.getOrganizationId());
        CognitoUser user;
        user = cognitoWrapper.createUserIfNotExistAndAddToGroup(body.getEmail(), CAREGIVER_GROUP_NAME, body.getPassword(), !body.isSuppressEmail());
        String caregiverId = CaregiverTable.ID_PREFIX + user.getId();

        Caregiver caregiver = Caregiver.builder()
                .pid(caregiverId)
                .sid(caregiverId)
                .email(body.getEmail())
                .firstName(body.getFirstName())
                .lastName(body.getLastName())
                .title(body.getTitle())
                .phoneNumber(body.getPhoneNumber())
                .build();
        caregiverDao.create(caregiver, body.getOrganizationId());

        return CreateCaregiverResponseBody.builder()
                .caregiverId(caregiverId)
                .password(user.getPassword())
                .build();
    }

    /**
     * Adds a Patient to a Caregiver. The Caregiver will be set as the primary Caregiver.
     * The adding process will only be completed after the patient accepts the add using the generated authCode.
     * Patient and Caregiver must already exist.
     *
     * @param body The request body
     * @return {@link AddPatientPrimaryResponseBody}
     * @throws RecordDoesNotExistException If Patient or Caregiver records do not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientEmail or caregiverId is empty or invalid
     */
    public AddPatientPrimaryResponseBody addPatientPrimary(AddPatientPrimaryRequestBody body) {
        log.info("Adding Patient to primary Caregiver {}", body);
        Validator.validateAddPatientPrimaryRequestBody(body);

        String authCode = generateAuthCode();
        caregiverDao.addPatientPrimary(body.getPatientEmail(), body.getCaregiverId(), authCode);

        if (body.isSendEmail()) {
            sesWrapper.caregiverAddPatientEmail(body.getPatientEmail(), body.getCaregiverId(), authCode);
        }

        return AddPatientPrimaryResponseBody.builder()
                .authCode(authCode)
                .build();
    }

    /**
     * Completes the process of adding a primary Caregiver to a Patient.
     *
     * @param body The request body
     * @return {@link AcceptPatientPrimaryResponseBody}
     * @throws RecordDoesNotExistException If Patient or Caregiver records do not exist, or if Caregiver is not
     *                                     unverified primary Caregiver of Patient
     * @throws InvalidAuthCodeException If the existing authCode does not match the provided authCode or is expired
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if caregiverId, patientId, or authCode are empty or invalid
     */
    public AcceptPatientPrimaryResponseBody acceptPatientPrimary(AcceptPatientPrimaryRequestBody body) {
        log.info("Accepting Caregiver as primary Caregiver for Patient {}", body);
        Validator.validateAcceptPatientPrimaryRequestBody(body);

        Caregiver caregiver = caregiverDao.findUnverifiedPrimaryCaregiver(body.getPatientId(), body.getCaregiverId());

        verifyAuthCode(caregiver.getAuthCode(), caregiver.getAuthCodeTimestamp(), body.getAuthCode());

        caregiverDao.acceptPatientPrimary(body.getPatientId(), body.getCaregiverId());

        return AcceptPatientPrimaryResponseBody.builder()
                .message("OK")
                .build();
    }

    /**
     * Adds a Patient to a Caregiver. Patient and Caregiver must already exist.
     *
     * @param body The request body
     * @return {@link AddPatientResponseBody}
     * @throws RecordDoesNotExistException If Patient or Caregiver records do not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if patientId or caregiverId is empty or invalid
     */
    public AddPatientResponseBody addPatient(AddPatientRequestBody body) {
        log.info("Adding Patient {}", body);
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
        log.info("Removing Patient {}", body);
        Validator.validateRemovePatientRequestBody(body);

        caregiverDao.removePatient(body.getPatientId(), body.getCaregiverId());

        return RemovePatientResponseBody.builder()
                .message("OK")
                .build();
    }

    /**
     * Gets the Caregiver specified by caregiverId and its Organization.
     *
     * @param body The request body
     * @return {@link GetCaregiverResponseBody}
     * @throws RecordDoesNotExistException If Caregiver record with the given caregiverId does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if caregiverId is empty
     */
    public GetCaregiverResponseBody getCaregiver(GetCaregiverRequestBody body) {
        log.info("Getting Caregiver {}", body);
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
     * @throws RecordDoesNotExistException If Caregiver record with the given caregiverId does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if caregiverId is empty
     */
    public GetAllPatientsResponseBody getAllPatients(GetAllPatientsRequestBody body) {
        log.info("Getting all Patients {}", body);
        Validator.validateGetAllPatientsRequestBody(body);

        caregiverDao.findById(body.getCaregiverId());
        List<Patient> patients = caregiverDao.findAllPatients(body.getCaregiverId());

        return GetAllPatientsResponseBody.builder()
                .patients(patients.stream().map(PatientSerialization::fromPatient).collect(Collectors.toList()))
                .build();
    }

    /**
     * Updates a Caregiver.
     *
     * @param body The request body
     * @return {@link UpdateCaregiverResponseBody}
     * @throws RecordDoesNotExistException If record with the given id does not exist
     * @throws IllegalArgumentException
     * @throws NullPointerException Above 2 exceptions are thrown if any of caregiverId, firstName, lastName,
     *                              title, or phoneNumber are empty
     */
    public UpdateCaregiverResponseBody updateCaregiver(UpdateCaregiverRequestBody body) {
        log.info("Updating Caregiver {}", body);
        Validator.validateUpdateCaregiverRequestBody(body);

        Caregiver caregiver = caregiverDao.findById(body.getCaregiverId());
        caregiver.setFirstName(body.getFirstName());
        caregiver.setLastName(body.getLastName());
        caregiver.setTitle(body.getTitle());
        caregiver.setPhoneNumber(body.getPhoneNumber());
        caregiverDao.update(caregiver);

        return UpdateCaregiverResponseBody.builder()
                .message("OK")
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
        log.info("Deleting Caregiver {}", body);
        Validator.validateDeleteCaregiverRequestBody(body);

        try {
            Caregiver caregiver = caregiverDao.findById(body.getCaregiverId());
            cognitoWrapper.removeUserFromGroupAndDeleteUser(caregiver.getEmail(), CAREGIVER_GROUP_NAME);
        } catch (Exception e) {
            log.warn("Error {} thrown when trying to find and delete Caregiver {} in Cognito", e.getClass(), body);
        }

        caregiverDao.delete(body.getCaregiverId());

        return DeleteCaregiverResponseBody.builder()
                .message("OK")
                .build();
    }

    /**
     * Primes the CaregiverService to reduce cold start time.
     */
    public void prime() {
        log.info("Priming CaregiverService");
        try {
            caregiverDao.findById("car-prime");
        } catch (Exception e) {
            // Expected
        }
        log.info("Done priming CaregiverService");
    }

    /**
     * Generates a random 6 digit numeric code between 100,000 (inclusive) and 1,000,000 (exclusive)
     *
     * @return {@link String}
     */
    private static String generateAuthCode() {
        Random rand = new Random();
        int code = rand.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private static void verifyAuthCode(String expected, String timestamp, String actual) {
        if (!expected.equals(actual)) {
            throw new InvalidAuthCodeException();
        }
        LocalDateTime authCodeTime = parseTime(timestamp);
        long secondsBetween = secondsBetweenTimes(authCodeTime, getCurrentUtcTime());
        if (secondsBetween > TTL) {
            throw new InvalidAuthCodeException();
        }
    }
}
