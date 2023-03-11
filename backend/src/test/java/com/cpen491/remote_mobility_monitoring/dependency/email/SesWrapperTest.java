package com.cpen491.remote_mobility_monitoring.dependency.email;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;

@ExtendWith(MockitoExtension.class)
public class SesWrapperTest {
    SesWrapper cut;
    @Mock
    SesV2Client sesV2Client;

    @BeforeEach
    public void setup() {
        cut = new SesWrapper(sesV2Client);
    }

//    @Test
    public void testTest() {
        SesV2Client client = SesV2Client.builder()
                .region(Region.US_WEST_2)
                .build();
        SesWrapper wrapper = new SesWrapper(client);
        wrapper.caregiverAddPatientEmail("danielnsyu@gmail.com", "car-123", "authCode");
    }
}
