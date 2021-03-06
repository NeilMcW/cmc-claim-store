package uk.gov.hmcts.cmc.domain.models.party;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.EqualsAndHashCode;
import uk.gov.hmcts.cmc.domain.constraints.AgeRangeValidator;
import uk.gov.hmcts.cmc.domain.models.Address;
import uk.gov.hmcts.cmc.domain.models.legalrep.Representative;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
public class Individual extends Party {

    @JsonUnwrapped
    @AgeRangeValidator
    private final LocalDate dateOfBirth;

    public Individual(
        String name,
        Address address,
        Address correspondenceAddress,
        String mobilePhone,
        Representative representative,
        LocalDate dateOfBirth
    ) {
        super(name, address, correspondenceAddress, mobilePhone, representative);
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

}
