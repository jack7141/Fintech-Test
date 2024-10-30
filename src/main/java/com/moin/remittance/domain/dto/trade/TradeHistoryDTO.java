package com.moin.remittance.domain.dto.trade;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TradeHistoryDTO {
    private long sourceAmount;
    private double fee;
    private double usdExchangeRate;
    private double usdAmount;
    private String targetCurrency;
    private double exchangeRate;
    private double targetAmount;
    private LocalDateTime requestedDate;
}
