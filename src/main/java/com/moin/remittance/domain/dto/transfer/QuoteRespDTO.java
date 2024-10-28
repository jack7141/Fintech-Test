package com.moin.remittance.domain.dto.transfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class QuoteRespDTO {
    private String quoteId;
    private BigDecimal exchangeRate;
    private String expireTime;
    private BigDecimal targetAmount;
}
