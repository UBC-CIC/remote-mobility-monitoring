package com.cpen491.remote_mobility_monitoring.dependency.email;

import software.amazon.awssdk.services.sesv2.SesV2Client;

public class SesWrapperFactory {
    private final String sender;
    private final SesV2Client sesClient;

    public SesWrapperFactory(String sender, SesV2Client sesClient) {
        this.sender = sender;
        this.sesClient = sesClient;
    }

    public SesWrapper createSesWrapper() {
        return new SesWrapper(sender, sesClient);
    }
}
