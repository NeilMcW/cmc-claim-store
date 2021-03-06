package uk.gov.hmcts.cmc.domain.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode
public class InterestAmount {

    private final BigDecimal amount;

    @JsonCreator
    public InterestAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
