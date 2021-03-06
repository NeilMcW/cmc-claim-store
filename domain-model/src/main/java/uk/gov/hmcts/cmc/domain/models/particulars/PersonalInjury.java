package uk.gov.hmcts.cmc.domain.models.particulars;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.validation.constraints.NotNull;

import static uk.gov.hmcts.cmc.domain.utils.ToStringStyle.ourStyle;

@EqualsAndHashCode
public class PersonalInjury {

    @NotNull
    private final DamagesExpectation generalDamages;

    @JsonCreator
    public PersonalInjury(DamagesExpectation generalDamages) {
        this.generalDamages = generalDamages;
    }

    public DamagesExpectation getGeneralDamages() {
        return generalDamages;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ourStyle());
    }

}
