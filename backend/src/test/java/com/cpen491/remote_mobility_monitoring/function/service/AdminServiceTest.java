package com.cpen491.remote_mobility_monitoring.function.service;

import com.cpen491.remote_mobility_monitoring.datastore.dao.AdminDao;
import com.cpen491.remote_mobility_monitoring.datastore.model.Admin;
import com.cpen491.remote_mobility_monitoring.datastore.model.Organization;
import com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper;
import com.cpen491.remote_mobility_monitoring.dependency.auth.CognitoWrapper.CognitoUser;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.CreateAdminRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.CreateAdminResponseBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.GetAdminRequestBody;
import com.cpen491.remote_mobility_monitoring.function.schema.admin.GetAdminResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static com.cpen491.remote_mobility_monitoring.TestUtils.assertInvalidInputExceptionThrown;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildAdmin;
import static com.cpen491.remote_mobility_monitoring.TestUtils.buildOrganization;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ADMIN_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ADMIN_ID_INVALID_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.CREATE_ADMIN_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.EMAIL_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.FIRST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.GET_ADMIN_NULL_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.LAST_NAME_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_BLANK_ERROR_MESSAGE;
import static com.cpen491.remote_mobility_monitoring.dependency.utility.Validator.ORGANIZATION_ID_INVALID_ERROR_MESSAGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    private static final String ADMIN_ID = "adm-1";
    private static final String ADMIN_ID_NO_PREFIX = "1";
    private static final String PASSWORD = "password123";
    private static final String EMAIL = "jackjackson@email.com";
    private static final String FIRST_NAME = "Jack";
    private static final String LAST_NAME = "Jackson";
    private static final String ORGANIZATION_ID = "org-1";
    private static final String ORGANIZATION_NAME = "ORG1";
    private static final String CREATED_AT = "2023-01-01";

    AdminService cut;
    @Mock
    AdminDao adminDao;
    @Mock
    CognitoWrapper cognitoWrapper;
    ArgumentCaptor<Admin> adminCaptor;

    @BeforeEach
    public void setup() {
        adminCaptor = ArgumentCaptor.forClass(Admin.class);
        cut = new AdminService(adminDao, cognitoWrapper);
    }

    @Test
    public void testCreateAdmin_HappyCase() {
        when(cognitoWrapper.createUser(anyString())).thenReturn(new CognitoUser(ADMIN_ID_NO_PREFIX, PASSWORD));

        CreateAdminRequestBody requestBody = buildCreateAdminRequestBody();
        CreateAdminResponseBody responseBody = cut.createAdmin(requestBody);

        verify(adminDao, times(1)).create(adminCaptor.capture(), eq(ORGANIZATION_ID));
        assertEquals(ADMIN_ID, adminCaptor.getValue().getPid());
        assertEquals(ADMIN_ID, adminCaptor.getValue().getSid());
        assertEquals(EMAIL, adminCaptor.getValue().getEmail());
        assertEquals(FIRST_NAME, adminCaptor.getValue().getFirstName());
        assertEquals(LAST_NAME, adminCaptor.getValue().getLastName());
        assertNotNull(responseBody);
        assertEquals(ADMIN_ID, responseBody.getAdminId());
        assertEquals(PASSWORD, responseBody.getPassword());
    }

    @Test
    public void testCreateAdmin_WHEN_CognitoWrapperThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(cognitoWrapper).createUser(anyString());

        CreateAdminRequestBody requestBody = buildCreateAdminRequestBody();
        assertThatThrownBy(() -> cut.createAdmin(requestBody)).isSameAs(toThrow);
    }

    @Test
    public void testCreateAdmin_WHEN_AdminDaoCreateThrows_THEN_ThrowSameException() {
        when(cognitoWrapper.createUser(anyString())).thenReturn(new CognitoUser(ADMIN_ID_NO_PREFIX, PASSWORD));

        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(adminDao).create(any(Admin.class), anyString());

        CreateAdminRequestBody requestBody = buildCreateAdminRequestBody();
        assertThatThrownBy(() -> cut.createAdmin(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForCreateAdmin")
    public void testCreateAdmin_WHEN_InvalidInput_THEN_ThrowInvalidInputException(CreateAdminRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.createAdmin(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForCreateAdmin() {
        return Stream.of(
                Arguments.of(null, CREATE_ADMIN_NULL_ERROR_MESSAGE),
                Arguments.of(buildCreateAdminRequestBody(null, FIRST_NAME, LAST_NAME, ORGANIZATION_ID), EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateAdminRequestBody("", FIRST_NAME, LAST_NAME, ORGANIZATION_ID), EMAIL_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateAdminRequestBody(EMAIL, null, LAST_NAME, ORGANIZATION_ID), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateAdminRequestBody(EMAIL, "", LAST_NAME, ORGANIZATION_ID), FIRST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateAdminRequestBody(EMAIL, FIRST_NAME, null, ORGANIZATION_ID), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateAdminRequestBody(EMAIL, FIRST_NAME, "", ORGANIZATION_ID), LAST_NAME_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateAdminRequestBody(EMAIL, FIRST_NAME, LAST_NAME, null), ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateAdminRequestBody(EMAIL, FIRST_NAME, LAST_NAME, ""), ORGANIZATION_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildCreateAdminRequestBody(EMAIL, FIRST_NAME, LAST_NAME, ADMIN_ID), ORGANIZATION_ID_INVALID_ERROR_MESSAGE)
        );
    }

    @Test
    public void testGetAdmin_HappyCase() {
        when(adminDao.findById(anyString())).thenReturn(buildAdminDefault());
        when(adminDao.findOrganization(anyString())).thenReturn(buildOrganizationDefault());

        GetAdminRequestBody requestBody = buildGetAdminRequestBody();
        GetAdminResponseBody responseBody = cut.getAdmin(requestBody);

        assertEquals(EMAIL, responseBody.getEmail());
        assertEquals(FIRST_NAME, responseBody.getFirstName());
        assertEquals(LAST_NAME, responseBody.getLastName());
        assertEquals(ORGANIZATION_ID, responseBody.getOrganizationId());
        assertEquals(ORGANIZATION_NAME, responseBody.getOrganizationName());
        assertEquals(CREATED_AT, responseBody.getCreatedAt());
    }

    @Test
    public void testGetAdmin_WHEN_AdminDaoFindByIdThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(adminDao).findById(anyString());

        GetAdminRequestBody requestBody = buildGetAdminRequestBody();
        assertThatThrownBy(() -> cut.getAdmin(requestBody)).isSameAs(toThrow);
    }

    @Test
    public void testGetAdmin_WHEN_AdminDaoFindOrganizationThrows_THEN_ThrowSameException() {
        NullPointerException toThrow = new NullPointerException();
        Mockito.doThrow(toThrow).when(adminDao).findOrganization(anyString());

        GetAdminRequestBody requestBody = buildGetAdminRequestBody();
        assertThatThrownBy(() -> cut.getAdmin(requestBody)).isSameAs(toThrow);
    }

    @ParameterizedTest
    @MethodSource("invalidInputsForGetAdmin")
    public void testGetAdmin_WHEN_InvalidInput_THEN_ThrowInvalidInputException(GetAdminRequestBody body, String errorMessage) {
        assertInvalidInputExceptionThrown(() -> cut.getAdmin(body), errorMessage);
    }

    private static Stream<Arguments> invalidInputsForGetAdmin() {
        return Stream.of(
                Arguments.of(null, GET_ADMIN_NULL_ERROR_MESSAGE),
                Arguments.of(buildGetAdminRequestBody(null), ADMIN_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildGetAdminRequestBody(""), ADMIN_ID_BLANK_ERROR_MESSAGE),
                Arguments.of(buildGetAdminRequestBody(ORGANIZATION_ID), ADMIN_ID_INVALID_ERROR_MESSAGE)
        );
    }

    private static CreateAdminRequestBody buildCreateAdminRequestBody() {
        return buildCreateAdminRequestBody(EMAIL, FIRST_NAME, LAST_NAME, ORGANIZATION_ID);
    }

    private static CreateAdminRequestBody buildCreateAdminRequestBody(String email, String firstName, String lastName, String organizationId) {
        return CreateAdminRequestBody.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .organizationId(organizationId)
                .build();
    }

    private static GetAdminRequestBody buildGetAdminRequestBody() {
        return buildGetAdminRequestBody(ADMIN_ID);
    }

    private static GetAdminRequestBody buildGetAdminRequestBody(String adminId) {
        return GetAdminRequestBody.builder()
                .adminId(adminId)
                .build();
    }

    private static Admin buildAdminDefault() {
        Admin admin = buildAdmin(ADMIN_ID, ADMIN_ID, EMAIL, FIRST_NAME, LAST_NAME);
        admin.setCreatedAt(CREATED_AT);
        return admin;
    }

    private static Organization buildOrganizationDefault() {
        return buildOrganization(ORGANIZATION_ID, ORGANIZATION_ID, ORGANIZATION_NAME);
    }
}
