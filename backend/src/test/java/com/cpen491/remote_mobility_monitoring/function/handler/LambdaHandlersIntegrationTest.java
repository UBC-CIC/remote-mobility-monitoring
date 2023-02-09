package com.cpen491.remote_mobility_monitoring.function.handler;

import com.cpen491.remote_mobility_monitoring.datastore.dao.GenericDao;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.dependency.utility.HandlerUtils.StatusCode;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.CreateCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetAllPatientsResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetAllPatientsResponseBody.PatientSerialization;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.GetCaregiverResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.caregiver.UpdateCaregiverRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.organization.GetOrganizationResponseBody.CaregiverSerialization;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.CreatePatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetAllCaregiversResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.GetPatientResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientDeviceResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.UpdatePatientRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.patient.VerifyPatientRequestBody;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminDeleteUserRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.cpen491.remote_mobility_monitoring.dependency.utility.TimeUtils.getCurrentUtcTimeString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LambdaHandlersIntegrationTest {
    private static final String TABLE_NAME = "REMOTE_MOBILITY_MONITORING-dev";
    private static final String COGNITO_USERPOOL_ID = "";
    private static final String BASE_URL = "";
    private static final String ORGANIZATION_ID = "org-1";
    private static final String NOT_EXISTS_ORGANIZATION_ID = "org-3535";
    private static final String ADMIN_ID = "adm-1";
    private static final String NOT_EXISTS_CAREGIVER_ID = "car-53135";
    private static final String CAREGIVER_EMAIL1 = "caregiverTest1@email.com";
    private static final String CAREGIVER_EMAIL2 = "caregiverTest2@email.com";
    private static final String CAREGIVER_NAME1 = "CaregiverTest1";
    private static final String CAREGIVER_NAME2 = "CaregiverTest2";
    private static final String CAREGIVER_UPDATED_NAME1 = "CaregiverTestOne";
    private static final String CAREGIVER_TITLE = "CaregiverTest";
    private static final String CAREGIVER_UPDATED_TITLE = "ManagerTest";
    private static final String CAREGIVER_PHONE_NUMBER = "+1231231234";
    private static final String NOT_EXISTS_PATIENT_ID = "pat-12345";
    private static final String PATIENT_NAME1 = "PatientTest1";
    private static final String PATIENT_NAME2 = "PatientTest2";
    private static final String PATIENT_UPDATED_NAME2 = "PatientTestTwo";
    private static final String PATIENT_NAME3 = "PatientTest3";
    private static final String DEVICE_ID1 = "device-test1";
    private static final String DEVICE_ID2 = "device-test2";
    private static final String DEVICE_ID_UPDATED2 = "device-testTwo";
    private static final String DEVICE_ID3 = "device-test3";
    private static final String PATIENT_PHONE_NUMBER = "+1010101010";
    private static final String INVALID_AUTH_CODE = "invalid";
    private static final String ORGANIZATION_NAME = "INTEGRATION_TEST_ORGANIZATION";
    private static final String CREATED_AT = getCurrentUtcTimeString();
    private static final Gson gson = new GsonBuilder().create();

    private static final String CAREGIVER_ID1_NAME = "CAREGIVER_ID1";
    private static final String CAREGIVER_ID2_NAME = "CAREGIVER_ID2";
    private static final String PATIENT_ID1_NAME = "PATIENT_ID1";
    private static final String PATIENT_ID2_NAME = "PATIENT_ID2";
    private static final String PATIENT_ID3_NAME = "PATIENT_ID3";
    private static final Map<String, String> idMap = new HashMap<>();

    private static GenericDao genericDao;
    private static HttpClient httpClient;

    @BeforeAll
    public static void setup() {
        DynamoDbClient ddbClient = DynamoDbClient.builder()
                .region(Region.US_WEST_2)
                .endpointOverride(URI.create("https://dynamodb.us-west-2.amazonaws.com"))
                .build();

        genericDao = new GenericDao(TABLE_NAME, ddbClient);

        Organization organization = Organization.builder()
                .pid(ORGANIZATION_ID)
                .sid(ORGANIZATION_ID)
                .name(ORGANIZATION_NAME)
                .createdAt(CREATED_AT)
                .updatedAt(CREATED_AT)
                .build();
        genericDao.put(Organization.convertToMap(organization));

        httpClient = HttpClient.newBuilder().build();
    }

    @AfterAll
    public static void teardown() {
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_WEST_2)
                .build();
        for (String email : new String[]{CAREGIVER_EMAIL1, CAREGIVER_EMAIL2}) {
            AdminDeleteUserRequest request = AdminDeleteUserRequest.builder()
                    .username(email)
                    .userPoolId(COGNITO_USERPOOL_ID)
                    .build();
            try {
                cognitoClient.adminDeleteUser(request);
            } catch (Exception e) {
                System.out.printf("User with email %s does not exist%n", email);
            }
        }

        genericDao.delete(ORGANIZATION_ID);
        for (String pid : idMap.values()) {
            genericDao.delete(pid);
        }
    }

    @Test
    @Order(1)
    public void testCreateTwoCaregivers() throws IOException, InterruptedException {
        HttpResponse<String> response;

        // Caregiver 1 should create successfully
        CreateCaregiverRequestBody createCaregiver1RequestBody = buildCreateCaregiver1RequestBody();
        response = sendPostRequest(BASE_URL + "/caregivers", gson.toJson(createCaregiver1RequestBody));
        assertEquals(StatusCode.OK.code, response.statusCode());
        CreateCaregiverResponseBody createCaregiver1ResponseBody = gson.fromJson(response.body(), CreateCaregiverResponseBody.class);
        String caregiverId1 = createCaregiver1ResponseBody.getCaregiverId();
        assertNotNull(caregiverId1);
        idMap.put(CAREGIVER_ID1_NAME, caregiverId1);

        // No email, should fail
        CreateCaregiverRequestBody createCaregiver2RequestBody = buildCreateCaregiver2RequestBody();
        createCaregiver2RequestBody.setEmail(null);
        response = sendPostRequest(BASE_URL + "/caregivers", gson.toJson(createCaregiver2RequestBody));
        assertEquals(StatusCode.BAD_REQUEST.code, response.statusCode());

        // Using Caregiver 1's email, should fail
        createCaregiver2RequestBody.setEmail(CAREGIVER_EMAIL1);
        response = sendPostRequest(BASE_URL + "/caregivers", gson.toJson(createCaregiver2RequestBody));
        assertEquals(StatusCode.BAD_REQUEST.code, response.statusCode());

        // Organization doesn't exist, should fail
        createCaregiver2RequestBody.setEmail(CAREGIVER_EMAIL2);
        createCaregiver2RequestBody.setOrganizationId(NOT_EXISTS_ORGANIZATION_ID);
        response = sendPostRequest(BASE_URL + "/caregivers", gson.toJson(createCaregiver2RequestBody));
        assertEquals(StatusCode.NOT_FOUND.code, response.statusCode());

        // Valid request body this time, Caregiver 2 should create successfully
        createCaregiver2RequestBody.setOrganizationId(ORGANIZATION_ID);
        response = sendPostRequest(BASE_URL + "/caregivers", gson.toJson(createCaregiver2RequestBody));
        assertEquals(StatusCode.OK.code, response.statusCode());
        CreateCaregiverResponseBody createCaregiver2ResponseBody = gson.fromJson(response.body(), CreateCaregiverResponseBody.class);
        String caregiverId2 = createCaregiver2ResponseBody.getCaregiverId();
        assertNotNull(caregiverId2);
        idMap.put(CAREGIVER_ID2_NAME, caregiverId2);

        // Get Organization to check whether Caregivers have been added successfully
        response = sendGetRequest(BASE_URL + "/organizations/" + ORGANIZATION_ID);
        assertEquals(StatusCode.OK.code, response.statusCode());
        GetOrganizationResponseBody GetOrganizationResponseBody = gson.fromJson(response.body(), GetOrganizationResponseBody.class);
        assertThat(GetOrganizationResponseBody.getCaregivers()).hasSize(2);
        assertThat(GetOrganizationResponseBody.getCaregivers().stream().map(CaregiverSerialization::getCaregiverId).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(caregiverId1, caregiverId2);
        assertThat(GetOrganizationResponseBody.getCaregivers().stream().map(CaregiverSerialization::getFirstName).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(CAREGIVER_NAME1, CAREGIVER_NAME2);

        // Not exists organization ID, should fail
        response = sendGetRequest(BASE_URL + "/organizations/" + NOT_EXISTS_ORGANIZATION_ID);
        assertEquals(StatusCode.NOT_FOUND.code, response.statusCode());
    }

    @Test
    @Order(2)
    public void testCreateAndVerifyThreePatients_AND_LinkThemToCaregiver1() throws IOException, InterruptedException {
        HttpResponse<String> response;

        // Patient 1 should create successfully
        CreatePatientRequestBody createPatient1RequestBody = buildCreatePatient1RequestBody();
        response = sendPostRequest(BASE_URL + "/patients", gson.toJson(createPatient1RequestBody));
        assertEquals(StatusCode.OK.code, response.statusCode());
        CreatePatientResponseBody createPatient1ResponseBody = gson.fromJson(response.body(), CreatePatientResponseBody.class);
        String patientId1 = createPatient1ResponseBody.getPatientId();
        String authCode1 = createPatient1ResponseBody.getAuthCode();
        assertNotNull(patientId1);
        assertNotNull(authCode1);
        idMap.put(PATIENT_ID1_NAME, patientId1);

        // No phone number, should fail
        CreatePatientRequestBody createPatient2RequestBody = buildCreatePatient2RequestBody();
        createPatient2RequestBody.setPhoneNumber(null);
        response = sendPostRequest(BASE_URL + "/patients", gson.toJson(createPatient2RequestBody));
        assertEquals(StatusCode.BAD_REQUEST.code, response.statusCode());

        // Valid request body this time, Patient 2 should create successfully
        createPatient2RequestBody.setPhoneNumber(PATIENT_PHONE_NUMBER);
        response = sendPostRequest(BASE_URL + "/patients", gson.toJson(createPatient2RequestBody));
        assertEquals(StatusCode.OK.code, response.statusCode());
        CreatePatientResponseBody createPatient2ResponseBody = gson.fromJson(response.body(), CreatePatientResponseBody.class);
        String patientId2 = createPatient2ResponseBody.getPatientId();
        String authCode2 = createPatient2ResponseBody.getAuthCode();
        assertNotNull(patientId2);
        assertNotNull(authCode2);
        idMap.put(PATIENT_ID2_NAME, patientId2);

        // Patient 3 should create successfully
        CreatePatientRequestBody createPatient3RequestBody = buildCreatePatient3RequestBody();
        response = sendPostRequest(BASE_URL + "/patients", gson.toJson(createPatient3RequestBody));
        assertEquals(StatusCode.OK.code, response.statusCode());
        CreatePatientResponseBody createPatient3ResponseBody = gson.fromJson(response.body(), CreatePatientResponseBody.class);
        String patientId3 = createPatient3ResponseBody.getPatientId();
        String authCode3 = createPatient3ResponseBody.getAuthCode();
        assertNotNull(patientId3);
        assertNotNull(authCode3);
        idMap.put(PATIENT_ID3_NAME, patientId3);

        // Verify Patient 1, should succeed
        VerifyPatientRequestBody verifyPatient1RequestBody = buildVerifyPatient1RequestBody(authCode1);
        response = sendPostRequest(BASE_URL + "/patients/" + patientId1 + "/verify", gson.toJson(verifyPatient1RequestBody));
        assertEquals(StatusCode.OK.code, response.statusCode());

        // No device ID, should fail
        VerifyPatientRequestBody verifyPatient2RequestBody = buildVerifyPatient2RequestBody(authCode2);
        verifyPatient2RequestBody.setDeviceId(null);
        response = sendPostRequest(BASE_URL + "/patients/" + patientId2 + "/verify", gson.toJson(verifyPatient2RequestBody));
        assertEquals(StatusCode.BAD_REQUEST.code, response.statusCode());

        // Duplicate device ID as Patient 1, should fail
        verifyPatient2RequestBody.setDeviceId(DEVICE_ID1);
        response = sendPostRequest(BASE_URL + "/patients/" + patientId2 + "/verify", gson.toJson(verifyPatient2RequestBody));
        assertEquals(StatusCode.BAD_REQUEST.code, response.statusCode());

        // Not exists caregiver ID, should fail
        verifyPatient2RequestBody.setDeviceId(DEVICE_ID2);
        verifyPatient2RequestBody.setCaregiverId(NOT_EXISTS_CAREGIVER_ID);
        response = sendPostRequest(BASE_URL + "/patients/" + patientId2 + "/verify", gson.toJson(verifyPatient2RequestBody));
        assertEquals(StatusCode.NOT_FOUND.code, response.statusCode());

        // Not exists patient ID, should fail
        verifyPatient2RequestBody.setCaregiverId(idMap.get(CAREGIVER_ID1_NAME));
        response = sendPostRequest(BASE_URL + "/patients/" + NOT_EXISTS_PATIENT_ID + "/verify", gson.toJson(verifyPatient2RequestBody));
        assertEquals(StatusCode.NOT_FOUND.code, response.statusCode());

        // Invalid auth code, should fail
        verifyPatient2RequestBody.setAuthCode(INVALID_AUTH_CODE);
        response = sendPostRequest(BASE_URL + "/patients/" + patientId2 + "/verify", gson.toJson(verifyPatient2RequestBody));
        assertEquals(StatusCode.UNAUTHORIZED.code, response.statusCode());

        // Verify Patient 2, should succeed
        verifyPatient2RequestBody.setAuthCode(authCode2);
        response = sendPostRequest(BASE_URL + "/patients/" + patientId2 + "/verify", gson.toJson(verifyPatient2RequestBody));
        assertEquals(StatusCode.OK.code, response.statusCode());

        // Verify Patient 3, should succeed
        VerifyPatientRequestBody verifyPatient3RequestBody = buildVerifyPatient3RequestBody(authCode3);
        response = sendPostRequest(BASE_URL + "/patients/" + patientId3 + "/verify", gson.toJson(verifyPatient3RequestBody));
        assertEquals(StatusCode.OK.code, response.statusCode());

        // Get all Patients to check whether Patients created successfully
        response = sendGetRequest(BASE_URL + "/caregivers/" + idMap.get(CAREGIVER_ID1_NAME) + "/patients");
        assertEquals(StatusCode.OK.code, response.statusCode());
        GetAllPatientsResponseBody getAllPatientsResponseBody = gson.fromJson(response.body(), GetAllPatientsResponseBody.class);
        assertThat(getAllPatientsResponseBody.getPatients()).hasSize(3);
        assertThat(getAllPatientsResponseBody.getPatients().stream().map(PatientSerialization::getPatientId).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(patientId1, patientId2, patientId3);
        assertThat(getAllPatientsResponseBody.getPatients().stream().map(PatientSerialization::getFirstName).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(PATIENT_NAME1, PATIENT_NAME2, PATIENT_NAME3);

        // Not exists caregiver ID, should fail
        response = sendGetRequest(BASE_URL + "/caregivers/" + NOT_EXISTS_CAREGIVER_ID + "/patients");
        assertEquals(StatusCode.NOT_FOUND.code, response.statusCode());
    }

    @Test
    @Order(3)
    public void testAddPatient3ToCaregiver2_AND_RemovePatient3FromCaregiver1() throws IOException, InterruptedException {
        HttpResponse<String> response;

        // Not exists patient ID, should fail
        String uri = BASE_URL + "/caregivers/" + idMap.get(CAREGIVER_ID2_NAME) + "/patients/" + NOT_EXISTS_PATIENT_ID;
        response = sendPostRequest(uri, "");
        assertEquals(StatusCode.NOT_FOUND.code, response.statusCode());

        // Not exists caregiver ID, should fail
        uri = BASE_URL + "/caregivers/" + NOT_EXISTS_CAREGIVER_ID + "/patients/" + idMap.get(PATIENT_ID3_NAME);
        response = sendPostRequest(uri, "");
        assertEquals(StatusCode.NOT_FOUND.code, response.statusCode());

        // Add Patient 3 to Caregiver 2, should succeed
        uri = BASE_URL + "/caregivers/" + idMap.get(CAREGIVER_ID2_NAME) + "/patients/" + idMap.get(PATIENT_ID3_NAME);
        response = sendPostRequest(uri, "");
        assertEquals(StatusCode.OK.code, response.statusCode());

        // Add Patient 3 to Caregiver 2 again, should fail
        response = sendPostRequest(uri, "");
        assertEquals(StatusCode.BAD_REQUEST.code, response.statusCode());

        // Remove Patient 3 from Caregiver 1, should succeed
        uri = BASE_URL + "/caregivers/" + idMap.get(CAREGIVER_ID1_NAME) + "/patients/" + idMap.get(PATIENT_ID3_NAME);
        response = sendDeleteRequest(uri);
        assertEquals(StatusCode.OK.code, response.statusCode());

        // Get all Patients of Caregiver 2 to check whether Patient 3 added successfully
        response = sendGetRequest(BASE_URL + "/caregivers/" + idMap.get(CAREGIVER_ID2_NAME) + "/patients");
        assertEquals(StatusCode.OK.code, response.statusCode());
        GetAllPatientsResponseBody getAllPatients2ResponseBody = gson.fromJson(response.body(), GetAllPatientsResponseBody.class);
        assertEquals(idMap.get(PATIENT_ID3_NAME), getAllPatients2ResponseBody.getPatients().get(0).getPatientId());

        // Get all Patients of Caregiver 1 to check whether Patient 3 removed successfully
        response = sendGetRequest(BASE_URL + "/caregivers/" + idMap.get(CAREGIVER_ID1_NAME) + "/patients");
        assertEquals(StatusCode.OK.code, response.statusCode());
        GetAllPatientsResponseBody getAllPatients1ResponseBody = gson.fromJson(response.body(), GetAllPatientsResponseBody.class);
        assertThat(getAllPatients1ResponseBody.getPatients().stream().map(PatientSerialization::getPatientId).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(idMap.get(PATIENT_ID1_NAME), idMap.get(PATIENT_ID2_NAME));

        // Get all Caregivers of Patient 3 to check whether it only has Caregiver 2
        response = sendGetRequest(BASE_URL + "/patients/" + idMap.get(PATIENT_ID3_NAME) + "/caregivers");
        assertEquals(StatusCode.OK.code, response.statusCode());
        GetAllCaregiversResponseBody getAllCaregiversResponseBody = gson.fromJson(response.body(), GetAllCaregiversResponseBody.class);
        assertEquals(idMap.get(CAREGIVER_ID2_NAME), getAllCaregiversResponseBody.getCaregivers().get(0).getCaregiverId());
    }

    @Test
    @Order(4)
    public void testUpdateCaregiversInformation() throws IOException, InterruptedException {
        HttpResponse<String> response;

        // Not exists caregiver ID, should fail
        UpdateCaregiverRequestBody updateCaregiver1RequestBody = UpdateCaregiverRequestBody.builder()
                .firstName(CAREGIVER_UPDATED_NAME1)
                .lastName(CAREGIVER_UPDATED_NAME1)
                .title(CAREGIVER_UPDATED_TITLE)
                .phoneNumber(CAREGIVER_PHONE_NUMBER)
                .build();
        response = sendPutRequest(BASE_URL + "/caregivers/" + NOT_EXISTS_CAREGIVER_ID, gson.toJson(updateCaregiver1RequestBody));
        assertEquals(StatusCode.NOT_FOUND.code, response.statusCode());

        // Update Caregiver 1, should succeed
        response = sendPutRequest(BASE_URL + "/caregivers/" + idMap.get(CAREGIVER_ID1_NAME), gson.toJson(updateCaregiver1RequestBody));
        assertEquals(StatusCode.OK.code, response.statusCode());

        // Get Caregiver 1 to check whether it is updated
        response = sendGetRequest(BASE_URL + "/caregivers/" + idMap.get(CAREGIVER_ID1_NAME));
        assertEquals(StatusCode.OK.code, response.statusCode());
        GetCaregiverResponseBody getCaregiverResponseBody = gson.fromJson(response.body(), GetCaregiverResponseBody.class);
        assertEquals(CAREGIVER_EMAIL1, getCaregiverResponseBody.getEmail());
        assertEquals(CAREGIVER_UPDATED_NAME1, getCaregiverResponseBody.getFirstName());
        assertEquals(CAREGIVER_UPDATED_NAME1, getCaregiverResponseBody.getLastName());
        assertEquals(CAREGIVER_UPDATED_TITLE, getCaregiverResponseBody.getTitle());

        // Get all Caregivers of Patient 1, should only be updated Caregiver 1
        response = sendGetRequest(BASE_URL + "/patients/" + idMap.get(PATIENT_ID1_NAME) + "/caregivers");
        assertEquals(StatusCode.OK.code, response.statusCode());
        GetAllCaregiversResponseBody getAllCaregiversResponseBody = gson.fromJson(response.body(), GetAllCaregiversResponseBody.class);
        assertThat(getAllCaregiversResponseBody.getCaregivers()).hasSize(1);
        assertEquals(idMap.get(CAREGIVER_ID1_NAME), getAllCaregiversResponseBody.getCaregivers().get(0).getCaregiverId());
        assertEquals(CAREGIVER_UPDATED_NAME1, getAllCaregiversResponseBody.getCaregivers().get(0).getFirstName());
        assertEquals(CAREGIVER_UPDATED_NAME1, getAllCaregiversResponseBody.getCaregivers().get(0).getFirstName());
    }

    @Test
    @Order(5)
    public void testUpdatePatientsInformation() throws IOException, InterruptedException {
        HttpResponse<String> response;

        // Not exists patient ID, should fail
        response = sendPostRequest(BASE_URL + "/patients/" + NOT_EXISTS_PATIENT_ID + "/device", "");
        assertEquals(StatusCode.NOT_FOUND.code, response.statusCode());

        // Update patient2 device, should succeed
        response = sendPostRequest(BASE_URL + "/patients/" + idMap.get(PATIENT_ID2_NAME) + "/device", "");
        assertEquals(StatusCode.OK.code, response.statusCode());
        UpdatePatientDeviceResponseBody updatePatientDeviceResponseBody = gson.fromJson(response.body(), UpdatePatientDeviceResponseBody.class);
        String authCode = updatePatientDeviceResponseBody.getAuthCode();

        // Not matching patient ID, should fail
        VerifyPatientRequestBody verifyPatient2RequestBody = buildVerifyPatient2RequestBody(authCode);
        verifyPatient2RequestBody.setDeviceId(DEVICE_ID_UPDATED2);
        response = sendPostRequest(BASE_URL + "/patients/" + idMap.get(PATIENT_ID1_NAME) + "/verify", gson.toJson(verifyPatient2RequestBody));
        assertEquals(StatusCode.UNAUTHORIZED.code, response.statusCode());

        // Verify Patient 2, should succeed
        verifyPatient2RequestBody.setDeviceId(DEVICE_ID_UPDATED2);
        response = sendPostRequest(BASE_URL + "/patients/" + idMap.get(PATIENT_ID2_NAME) + "/verify", gson.toJson(verifyPatient2RequestBody));
        assertEquals(StatusCode.OK.code, response.statusCode());

        // Get Patient 2 to check whether it is updated
        response = sendGetRequest(BASE_URL + "/patients/" + idMap.get(PATIENT_ID2_NAME));
        assertEquals(StatusCode.OK.code, response.statusCode());
        GetPatientResponseBody getPatientResponseBody = gson.fromJson(response.body(), GetPatientResponseBody.class);
        assertEquals(DEVICE_ID_UPDATED2, getPatientResponseBody.getDeviceId());

        // Not exists patient ID, should fail
        UpdatePatientRequestBody updatePatient2RequestBody = UpdatePatientRequestBody.builder()
                .firstName(PATIENT_UPDATED_NAME2)
                .lastName(PATIENT_UPDATED_NAME2)
                .phoneNumber(PATIENT_PHONE_NUMBER)
                .build();
        response = sendPutRequest(BASE_URL + "/patients/" + NOT_EXISTS_PATIENT_ID, gson.toJson(updatePatient2RequestBody));
        assertEquals(StatusCode.NOT_FOUND.code, response.statusCode());

        // Update Patient 2, should succeed
        response = sendPutRequest(BASE_URL + "/patients/" + idMap.get(PATIENT_ID2_NAME), gson.toJson(updatePatient2RequestBody));
        assertEquals(StatusCode.OK.code, response.statusCode());

        // Get Patient 2 to check whether it is updated
        response = sendGetRequest(BASE_URL + "/patients/" + idMap.get(PATIENT_ID2_NAME));
        assertEquals(StatusCode.OK.code, response.statusCode());
        getPatientResponseBody = gson.fromJson(response.body(), GetPatientResponseBody.class);
        assertEquals(DEVICE_ID_UPDATED2, getPatientResponseBody.getDeviceId());
        assertEquals(PATIENT_UPDATED_NAME2, getPatientResponseBody.getFirstName());
        assertEquals(PATIENT_UPDATED_NAME2, getPatientResponseBody.getLastName());

        // Get all Patients of Caregiver 1 to check whether Patient 2 is updated
        response = sendGetRequest(BASE_URL + "/caregivers/" + idMap.get(CAREGIVER_ID1_NAME) + "/patients");
        assertEquals(StatusCode.OK.code, response.statusCode());
        GetAllPatientsResponseBody getAllPatients1ResponseBody = gson.fromJson(response.body(), GetAllPatientsResponseBody.class);
        assertThat(getAllPatients1ResponseBody.getPatients().stream().map(PatientSerialization::getFirstName).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(PATIENT_NAME1, PATIENT_UPDATED_NAME2);
    }

    @Test
    @Order(6)
    public void testDeleteCaregiver1() throws IOException, InterruptedException {
        HttpResponse<String> response;

        // Get all Caregivers of Patient 1, should only be updated Caregiver 1
        response = sendGetRequest(BASE_URL + "/patients/" + idMap.get(PATIENT_ID1_NAME) + "/caregivers");
        assertEquals(StatusCode.OK.code, response.statusCode());
        GetAllCaregiversResponseBody getAllCaregiversResponseBody = gson.fromJson(response.body(), GetAllCaregiversResponseBody.class);
        assertThat(getAllCaregiversResponseBody.getCaregivers()).hasSize(1);
        assertEquals(idMap.get(CAREGIVER_ID1_NAME), getAllCaregiversResponseBody.getCaregivers().get(0).getCaregiverId());

        // Get all Caregivers of Patient 2, should only be updated Caregiver 1
        response = sendGetRequest(BASE_URL + "/patients/" + idMap.get(PATIENT_ID2_NAME) + "/caregivers");
        assertEquals(StatusCode.OK.code, response.statusCode());
        getAllCaregiversResponseBody = gson.fromJson(response.body(), GetAllCaregiversResponseBody.class);
        assertThat(getAllCaregiversResponseBody.getCaregivers()).hasSize(1);
        assertEquals(idMap.get(CAREGIVER_ID1_NAME), getAllCaregiversResponseBody.getCaregivers().get(0).getCaregiverId());

        // Delete Caregiver 1, should succeed
        response = sendDeleteRequest(BASE_URL + "/caregivers/" + idMap.get(CAREGIVER_ID1_NAME));
        assertEquals(StatusCode.OK.code, response.statusCode());

        // Get Caregiver 1 to check whether it is deleted
        response = sendGetRequest(BASE_URL + "/caregivers/" + idMap.get(CAREGIVER_ID1_NAME));
        assertEquals(StatusCode.NOT_FOUND.code, response.statusCode());

        // Get all Caregivers of Patient 1, should be empty
        response = sendGetRequest(BASE_URL + "/patients/" + idMap.get(PATIENT_ID1_NAME) + "/caregivers");
        assertEquals(StatusCode.OK.code, response.statusCode());
        getAllCaregiversResponseBody = gson.fromJson(response.body(), GetAllCaregiversResponseBody.class);
        assertThat(getAllCaregiversResponseBody.getCaregivers()).hasSize(0);

        // Get all Caregivers of Patient 2, should be empty
        response = sendGetRequest(BASE_URL + "/patients/" + idMap.get(PATIENT_ID2_NAME) + "/caregivers");
        assertEquals(StatusCode.OK.code, response.statusCode());
        getAllCaregiversResponseBody = gson.fromJson(response.body(), GetAllCaregiversResponseBody.class);
        assertThat(getAllCaregiversResponseBody.getCaregivers()).hasSize(0);

        // Get Organization to check whether Caregiver 1 has been deleted successfully
        response = sendGetRequest(BASE_URL + "/organizations/" + ORGANIZATION_ID);
        assertEquals(StatusCode.OK.code, response.statusCode());
        GetOrganizationResponseBody GetOrganizationResponseBody = gson.fromJson(response.body(), GetOrganizationResponseBody.class);
        assertThat(GetOrganizationResponseBody.getCaregivers()).hasSize(1);
        assertEquals(idMap.get(CAREGIVER_ID2_NAME), GetOrganizationResponseBody.getCaregivers().get(0).getCaregiverId());
    }

    @Test
    @Order(7)
    public void testDeletePatient3() throws IOException, InterruptedException {
        HttpResponse<String> response;

        // Get all Patients of Caregiver 2, should only be Patient 3
        response = sendGetRequest(BASE_URL + "/caregivers/" + idMap.get(CAREGIVER_ID2_NAME) + "/patients");
        assertEquals(StatusCode.OK.code, response.statusCode());
        GetAllPatientsResponseBody getAllPatients1ResponseBody = gson.fromJson(response.body(), GetAllPatientsResponseBody.class);
        assertThat(getAllPatients1ResponseBody.getPatients()).hasSize(1);
        assertEquals(idMap.get(PATIENT_ID3_NAME), getAllPatients1ResponseBody.getPatients().get(0).getPatientId());

        // Delete Patient 3, should succeed
        response = sendDeleteRequest(BASE_URL + "/patients/" + idMap.get(PATIENT_ID3_NAME));
        assertEquals(StatusCode.OK.code, response.statusCode());

        // Get Patient 3 to check whether it is deleted
        response = sendGetRequest(BASE_URL + "/patients/" + idMap.get(PATIENT_ID3_NAME));
        assertEquals(StatusCode.NOT_FOUND.code, response.statusCode());

        // Get all Patients of Caregiver 2, should be empty
        response = sendGetRequest(BASE_URL + "/caregivers/" + idMap.get(CAREGIVER_ID2_NAME) + "/patients");
        assertEquals(StatusCode.OK.code, response.statusCode());
        getAllPatients1ResponseBody = gson.fromJson(response.body(), GetAllPatientsResponseBody.class);
        assertThat(getAllPatients1ResponseBody.getPatients()).hasSize(0);
    }

    private static HttpRequest buildPostRequest(String uri, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private static HttpRequest buildPutRequest(String uri, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private static HttpRequest buildGetRequest(String uri) {
        return HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .GET()
                .build();
    }

    private static HttpRequest buildDeleteRequest(String uri) {
        return HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .DELETE()
                .build();
    }

    private static HttpResponse<String> sendPostRequest(String uri, String body) throws IOException, InterruptedException {
        HttpRequest request = buildPostRequest(uri, body);
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> sendGetRequest(String uri) throws IOException, InterruptedException {
        HttpRequest request = buildGetRequest(uri);
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> sendPutRequest(String uri, String body) throws IOException, InterruptedException {
        HttpRequest request = buildPutRequest(uri, body);
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> sendDeleteRequest(String uri) throws IOException, InterruptedException {
        HttpRequest request = buildDeleteRequest(uri);
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static CreateCaregiverRequestBody buildCreateCaregiver1RequestBody() {
        return buildCreateCaregiverRequestBody(CAREGIVER_EMAIL1, CAREGIVER_NAME1, CAREGIVER_NAME1, CAREGIVER_TITLE, CAREGIVER_PHONE_NUMBER, ORGANIZATION_ID);
    }

    private static CreateCaregiverRequestBody buildCreateCaregiver2RequestBody() {
        return buildCreateCaregiverRequestBody(CAREGIVER_EMAIL2, CAREGIVER_NAME2, CAREGIVER_NAME2, CAREGIVER_TITLE, CAREGIVER_PHONE_NUMBER, ORGANIZATION_ID);
    }

    private static CreateCaregiverRequestBody buildCreateCaregiverRequestBody(String email, String firstName, String lastName,
                                                                               String title, String phoneNumber, String organizationId) {
        return CreateCaregiverRequestBody.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .title(title)
                .phoneNumber(phoneNumber)
                .organizationId(organizationId)
                .build();
    }

    private static CreatePatientRequestBody buildCreatePatient1RequestBody() {
        return buildCreatePatientRequestBody(PATIENT_NAME1, PATIENT_NAME1, PATIENT_PHONE_NUMBER);
    }

    private static CreatePatientRequestBody buildCreatePatient2RequestBody() {
        return buildCreatePatientRequestBody(PATIENT_NAME2, PATIENT_NAME2, PATIENT_PHONE_NUMBER);
    }

    private static CreatePatientRequestBody buildCreatePatient3RequestBody() {
        return buildCreatePatientRequestBody(PATIENT_NAME3, PATIENT_NAME3, PATIENT_PHONE_NUMBER);
    }

    private static CreatePatientRequestBody buildCreatePatientRequestBody(String firstName, String lastName, String phoneNumber) {
        return CreatePatientRequestBody.builder()
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .build();
    }

    private static VerifyPatientRequestBody buildVerifyPatient1RequestBody(String authCode1) {
        return buildVerifyPatientRequestBody(idMap.get(CAREGIVER_ID1_NAME), authCode1, DEVICE_ID1);
    }

    private static VerifyPatientRequestBody buildVerifyPatient2RequestBody(String authCode2) {
        return buildVerifyPatientRequestBody(idMap.get(CAREGIVER_ID1_NAME), authCode2, DEVICE_ID2);
    }

    private static VerifyPatientRequestBody buildVerifyPatient3RequestBody(String authCode3) {
        return buildVerifyPatientRequestBody(idMap.get(CAREGIVER_ID1_NAME), authCode3, DEVICE_ID3);
    }

    private static VerifyPatientRequestBody buildVerifyPatientRequestBody(String caregiverId, String authCode, String deviceId) {
        return VerifyPatientRequestBody.builder()
                .caregiverId(caregiverId)
                .authCode(authCode)
                .deviceId(deviceId)
                .build();
    }
}
