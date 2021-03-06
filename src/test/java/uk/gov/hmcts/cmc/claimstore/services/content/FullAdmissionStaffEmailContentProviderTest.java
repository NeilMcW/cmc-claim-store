package uk.gov.hmcts.cmc.claimstore.services.content;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.cmc.claimstore.config.PebbleConfiguration;
import uk.gov.hmcts.cmc.claimstore.config.properties.emails.StaffEmailTemplates;
import uk.gov.hmcts.cmc.claimstore.services.TemplateService;
import uk.gov.hmcts.cmc.claimstore.services.staff.content.FullAdmissionStaffEmailContentProvider;
import uk.gov.hmcts.cmc.claimstore.services.staff.models.EmailContent;
import uk.gov.hmcts.cmc.domain.models.Claim;
import uk.gov.hmcts.cmc.domain.models.sampledata.SampleClaim;
import uk.gov.hmcts.cmc.domain.models.sampledata.SampleResponse;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.cmc.claimstore.services.staff.DefendantResponseStaffNotificationService.wrapInMap;

public class FullAdmissionStaffEmailContentProviderTest {

    private static final String DEFENDANT_EMAIL = "defendant@mail.com";

    private TemplateService templateService = new TemplateService(
        new PebbleConfiguration().pebbleEngine()
    );

    private StaffEmailTemplates templates = new StaffEmailTemplates();

    private FullAdmissionStaffEmailContentProvider service;

    @Before
    public void beforeEachTest() {
        service = new FullAdmissionStaffEmailContentProvider(
            templateService,
            templates
        );
    }

    @Test
    public void shouldUsePaymentOptionByImmediately() {
        Claim claimWithFullAdmission = SampleClaim.builder()
            .withResponse(SampleResponse.FullAdmission.builder().buildWithPaymentOptionImmediately())
            .withRespondedAt(LocalDateTime.now())
            .build();
        EmailContent content = service.createContent(wrapInMap(claimWithFullAdmission, DEFENDANT_EMAIL));
        assertThat(content.getSubject())
            .contains("Pay immediately 000CM001:")
            .contains("John Rambo v John Smith");

        assertThat(content.getBody())
            .contains("The defendant has offered to pay immediately in response to the ")

            .contains("money claim made against them by John Rambo.");
    }

    @Test
    public void shouldUsePaymentOptionByInstalments() {
        Claim claimWithFullAdmission = SampleClaim.builder()
            .withResponse(SampleResponse.FullAdmission.builder().build())
            .withRespondedAt(LocalDateTime.now())
            .build();
        EmailContent content = service.createContent(wrapInMap(claimWithFullAdmission, DEFENDANT_EMAIL));

        assertThat(content.getSubject())
            .contains("Pay by instalments 000CM001: John Rambo v John Smith");
        assertThat(content.getBody())
            .contains("The defendant has offered to pay by instalments in response to the ")
            .contains("money claim made against them by John Rambo.");
    }

    @Test
    public void shouldUsePaymentOptionBySetDate() {
        Claim claimWithFullAdmission = SampleClaim.builder()
            .withResponse(SampleResponse.FullAdmission.builder().buildWithPaymentOptionBySpecifiedDate())
            .withRespondedAt(LocalDateTime.now())
            .build();
        EmailContent content = service.createContent(wrapInMap(claimWithFullAdmission, DEFENDANT_EMAIL));

        assertThat(content.getSubject())
            .contains("Pay by a set date 000CM001: John Rambo v John Smith");
        assertThat(content.getBody())
            .contains("The defendant has offered to pay by a set date in response to the ")
            .contains("money claim made against them by John Rambo.");
    }
}
