package com.cpen491.remote_mobility_monitoring.dependency.email;

import com.cpen491.remote_mobility_monitoring.dependency.exception.SesException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;

@Slf4j
@RequiredArgsConstructor
public class SesWrapper {
    private static final String sender = "";

    @NonNull
    private SesV2Client sesClient;

    public void caregiverAddPatientEmail(String patientEmail, String caregiverId, String authCode) {
        log.info("Sending Caregiver {} add Patient {} as primary Caregiver email with authCode {}",
                caregiverId, patientEmail, authCode);

        Destination destination = Destination.builder()
                .toAddresses(patientEmail)
                .build();

        String bodyHtml = "<html><head></head><body><p>Click </p><a href=\"com.myApp://home\">here</a></body></html>";
        Content content = Content.builder()
                .data(bodyHtml)
                .build();

        Content subject = Content.builder()
                .data("Primary caregiver request")
                .build();

        Body body = Body.builder()
                .html(content)
                .build();

        Message message = Message.builder()
                .subject(subject)
                .body(body)
                .build();

        EmailContent emailContent = EmailContent.builder()
                .simple(message)
                .build();

        SendEmailRequest request = SendEmailRequest.builder()
                .destination(destination)
                .content(emailContent)
                .fromEmailAddress(sender)
                .build();

        try {
            sesClient.sendEmail(request);
        } catch (SesV2Exception e) {
            log.error("Encountered Ses error", e);
            throw new SesException(e);
        }
    }
}
