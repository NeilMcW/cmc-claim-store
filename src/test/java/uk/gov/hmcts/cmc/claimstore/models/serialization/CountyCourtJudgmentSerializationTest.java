package uk.gov.hmcts.cmc.claimstore.models.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import uk.gov.hmcts.cmc.claimstore.config.JacksonConfiguration;
import uk.gov.hmcts.cmc.claimstore.models.CountyCourtJudgment;
import uk.gov.hmcts.cmc.claimstore.models.otherparty.TheirDetails;
import uk.gov.hmcts.cmc.claimstore.models.sampledata.SampleCountyCourtJudgment;
import uk.gov.hmcts.cmc.claimstore.models.sampledata.SampleRepaymentPlan;
import uk.gov.hmcts.cmc.claimstore.models.sampledata.SampleTheirDetails;
import uk.gov.hmcts.cmc.claimstore.utils.ResourceReader;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class CountyCourtJudgmentSerializationTest {

    private static final ObjectMapper mapper = new JacksonConfiguration().objectMapper();

    @Test
    public void shouldConvertCCJJsonToJava() throws IOException {

        //given
        TheirDetails defendant = SampleTheirDetails.builder().individualDetails();
        CountyCourtJudgment expected = SampleCountyCourtJudgment.builder()
            .withDefendant(defendant)
            .withRepaymentPlan(SampleRepaymentPlan.builder().build())
            .build();

        //when
        CountyCourtJudgment other = jsonToModel("/county-court-judgment/by-instalments.json");

        //then
        assertThat(expected).isEqualTo(other);
    }

    private static CountyCourtJudgment jsonToModel(final String path) throws IOException {
        final String json = new ResourceReader().read(path);
        return mapper.readValue(json, CountyCourtJudgment.class);
    }
}
