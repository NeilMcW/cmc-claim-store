package uk.gov.hmcts.cmc.claimstore.tests.functional.citizen;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.cmc.claimstore.idam.models.User;
import uk.gov.hmcts.cmc.claimstore.tests.BaseTest;
import uk.gov.hmcts.cmc.domain.models.Claim;
import uk.gov.hmcts.cmc.domain.models.response.DefenceType;
import uk.gov.hmcts.cmc.domain.models.response.Response;
import uk.gov.hmcts.cmc.domain.models.response.YesNoOption;
import uk.gov.hmcts.cmc.domain.models.sampledata.SampleResponse;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static uk.gov.hmcts.cmc.domain.utils.LocalDateTimeFactory.nowInLocalZone;

public class RespondToClaimTest extends BaseTest {

    private User claimant;

    @Before
    public void before() {
        claimant = idamTestService.createCitizen();
    }

    @Test
    public void shouldBeAbleToSuccessfullySubmitDisputeDefence() {
        Response fullDefenceDisputeResponse = SampleResponse.FullDefence
            .builder()
            .withDefenceType(DefenceType.DISPUTE)
            .withMediation(null)
            .build();
        shouldBeAbleToSuccessfullySubmit(fullDefenceDisputeResponse);
    }


    @Test
    public void shouldBeAbleToSuccessfullySubmitFreeMediationRequestOnDefence() {
        Response fullDefenceDisputeResponse = SampleResponse.FullDefence
            .builder()
            .withDefenceType(DefenceType.DISPUTE)
            .withMediation(YesNoOption.YES)
            .build();

        shouldBeAbleToSuccessfullySubmit(fullDefenceDisputeResponse);
    }

    @Test
    public void shouldBeAbleToSuccessfullySubmitAlreadyPaidDefence() {
        Response fullDefenceAlreadyPaidResponse = SampleResponse.FullDefence.builder()
            .withDefenceType(DefenceType.ALREADY_PAID)
            .withMediation(null)
            .build();
        shouldBeAbleToSuccessfullySubmit(fullDefenceAlreadyPaidResponse);
    }

    @Test
    public void shouldBeAbleToSuccessfullySubmitFullAdmission() {
        Response fullAdmissionResponse = SampleResponse.FullAdmission.builder().build();
        shouldBeAbleToSuccessfullySubmit(fullAdmissionResponse);
    }

    @Test
    public void shouldBeAbleToSuccessfullySubmitPartAdmissionWithAlreadyPaidAmount() {
        Response partAdmissionResponse = SampleResponse.PartAdmission.builder().build();
        shouldBeAbleToSuccessfullySubmit(partAdmissionResponse);
    }

    @Test
    public void shouldBeAbleToSuccessfullySubmitPartAdmissionWithPaymentInFuture() {
        Response partAdmissionResponse = SampleResponse.PartAdmission.builder().buildWithPaymentOptionBySpecifiedDate();
        shouldBeAbleToSuccessfullySubmit(partAdmissionResponse);
    }

    private void shouldBeAbleToSuccessfullySubmit(Response response) {
        String claimantId = claimant.getUserDetails().getId();
        Claim createdCase = commonOperations.submitClaim(
            claimant.getAuthorisation(),
            claimantId
        );

        User defendant = idamTestService.createDefendant(createdCase.getLetterHolderId());
        commonOperations.linkDefendant(
            defendant.getAuthorisation()
        );

        Claim updatedCase = commonOperations.submitResponse(response, createdCase.getExternalId(), defendant)
            .then()
            .statusCode(HttpStatus.OK.value())
            .and()
            .extract().body().as(Claim.class);

        assertThat(updatedCase.getResponse().isPresent()).isTrue();
        assertThat(updatedCase.getResponse().get()).isEqualTo(response);
        assertThat(updatedCase.getRespondedAt()).isCloseTo(nowInLocalZone(), within(2, ChronoUnit.MINUTES));
    }

    @Test
    public void shouldReturnUnprocessableEntityWhenInvalidResponseIsSubmitted() {
        String claimantId = claimant.getUserDetails().getId();
        Claim createdCase = commonOperations.submitClaim(
            claimant.getAuthorisation(),
            claimantId
        );

        User defendant = idamTestService.createDefendant(createdCase.getLetterHolderId());
        commonOperations.linkDefendant(
            defendant.getAuthorisation()
        );

        Response invalidResponse = SampleResponse.FullDefence.builder()
            .withDefenceType(null)
            .build();

        commonOperations.submitResponse(invalidResponse, createdCase.getExternalId(), defendant)
            .then()
            .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
    }
}
