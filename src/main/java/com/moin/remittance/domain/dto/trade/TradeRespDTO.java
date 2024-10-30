package com.moin.remittance.domain.dto.trade;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TradeRespDTO {
    private String userId;
    private String name;
    private int todayTransferCount;
    private double todayTransferUsdAmount;
    private List<TradeHistoryDTO> history;
}
