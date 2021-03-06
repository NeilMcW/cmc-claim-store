package uk.gov.hmcts.cmc.domain.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.hibernate.validator.constraints.NotBlank;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

import static uk.gov.hmcts.cmc.domain.utils.ToStringStyle.ourStyle;

@EqualsAndHashCode
@JsonIgnoreProperties(value = {"description", "state"})
public class Payment {
    private final String id;
    /**
     * The amount which was paid, in pennies for payments v1 or pounds with payments v2.
     */
    @NotNull
    private final BigDecimal amount;
    @NotBlank
    private final String reference;
    @JsonProperty("date_created")
    private final String dateCreated;
    private final String status;

    public Payment(
        String id,
        BigDecimal amount,
        String reference,
        String dateCreated,
        String status
    ) {
        this.id = id;
        this.amount = amount;
        this.reference = reference;
        this.dateCreated = dateCreated;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getReference() {
        return reference;
    }

    public String getStatus() {
        return status;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ourStyle());
    }
}
