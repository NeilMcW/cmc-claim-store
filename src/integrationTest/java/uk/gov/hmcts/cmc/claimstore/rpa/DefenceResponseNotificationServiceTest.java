package uk.gov.hmcts.cmc.claimstore.rpa;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import uk.gov.hmcts.cmc.claimstore.MockSpringTest;
import uk.gov.hmcts.cmc.claimstore.events.response.DefendantResponseEvent;
import uk.gov.hmcts.cmc.claimstore.rpa.config.EmailProperties;
import uk.gov.hmcts.cmc.claimstore.services.staff.DefendantResponseStaffNotificationService;
import uk.gov.hmcts.cmc.domain.models.Claim;
import uk.gov.hmcts.cmc.domain.models.sampledata.SampleClaim;
import uk.gov.hmcts.cmc.domain.models.sampledata.SampleResponse;
import uk.gov.hmcts.cmc.email.EmailAttachment;
import uk.gov.hmcts.cmc.email.EmailData;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.cmc.claimstore.documents.output.PDF.EXTENSION;
import static uk.gov.hmcts.cmc.claimstore.rpa.ClaimIssuedNotificationService.JSON_EXTENSION;
import static uk.gov.hmcts.cmc.claimstore.utils.DocumentNameUtils.buildJsonResponseFileBaseName;
import static uk.gov.hmcts.cmc.claimstore.utils.DocumentNameUtils.buildResponseFileBaseName;

public class DefenceResponseNotificationServiceTest extends MockSpringTest {
    private static final byte[] PDF_CONTENT = {1, 2, 3, 4};

    @Autowired
    private DefenceResponseNotificationService service;
    @Autowired
    private EmailProperties emailProperties;

    @MockBean
    private DefendantResponseStaffNotificationService defendantResponseStaffNotificationService;

    @Captor
    private ArgumentCaptor<String> senderArgument;

    @Captor
    private ArgumentCaptor<EmailData> emailDataArgument;

    @Captor
    private ArgumentCaptor<Claim> claimArgumentCaptor;

    private Claim claim;

    private DefendantResponseEvent event;

    @Before
    public void setUp() {
        claim = SampleClaim
            .builder()
            .withResponse(SampleResponse.validDefaults())
            .withRespondedAt(LocalDateTime.of(2018, 4, 26, 1, 1))
            .build();

        event = new DefendantResponseEvent(claim);

    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerWhenGivenNullClaim() {
        service.notifyRobotics(null);
    }

    @Test
    public void shouldSendResponseEmailWithConfiguredValues() {
        commonCreatePDFExpectation();

        service.notifyRobotics(event);

        verify(emailService).sendEmail(senderArgument.capture(), emailDataArgument.capture());
        verify(defendantResponseStaffNotificationService).createResponsePdfAttachment(claimArgumentCaptor.capture());

        assertThat(senderArgument.getValue()).isEqualTo(emailProperties.getSender());
        assertThat(emailDataArgument.getValue().getTo()).isEqualTo(emailProperties.getResponseRecipient());
        assertThat(emailDataArgument.getValue().getSubject()).isEqualToIgnoringNewLines("J defence response 000CM001");
        assertThat(emailDataArgument.getValue().getMessage()).isEmpty();
    }

    @Test
    public void shouldSendEmailWithConfiguredValuesAndAttachments() {
        commonCreatePDFExpectation();

        service.notifyRobotics(event);

        verify(emailService).sendEmail(senderArgument.capture(), emailDataArgument.capture());

        EmailAttachment responsePdfAttachment = emailDataArgument.getValue()
            .getAttachments()
            .get(0);

        String expectedPdfFilename = buildResponseFileBaseName(claim.getReferenceNumber()) + EXTENSION;

        assertThat(responsePdfAttachment.getContentType()).isEqualTo(MediaType.APPLICATION_PDF_VALUE);
        assertThat(responsePdfAttachment.getFilename()).isEqualTo(expectedPdfFilename);

        EmailAttachment responseJsonAttachment = emailDataArgument.getValue()
            .getAttachments()
            .get(1);

        String expectedDefenceJsonFilename = buildJsonResponseFileBaseName(claim.getReferenceNumber()) + JSON_EXTENSION;

        assertThat(responseJsonAttachment.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(responseJsonAttachment.getFilename()).isEqualTo(expectedDefenceJsonFilename);
    }

    private void commonCreatePDFExpectation() {
        when(defendantResponseStaffNotificationService
            .createResponsePdfAttachment(claim))
            .thenReturn(EmailAttachment.pdf(PDF_CONTENT,
                buildResponseFileBaseName(claim.getReferenceNumber()) + EXTENSION));
    }

}
