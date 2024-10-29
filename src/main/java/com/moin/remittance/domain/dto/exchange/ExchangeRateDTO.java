package com.moin.remittance.domain.dto.exchange;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateDTO {

    @JsonProperty
    private String code;

    @JsonProperty
    private String currencyCode;

    @JsonProperty
    private double basePrice;

    @JsonProperty
    private int currencyUnit;


}
