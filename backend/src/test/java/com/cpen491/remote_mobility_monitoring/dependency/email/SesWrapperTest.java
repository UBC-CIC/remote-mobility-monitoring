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
        cut = new SesWrapper("user@email.com", sesV2Client);
    }
}
