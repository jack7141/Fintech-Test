package com.moin.remittance.domain.dto.response;

import com.moin.remittance.domain.dto.trade.TradeHistoryDTO;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString(callSuper = true)
public class TradeHistResponseDTO extends ResponseDTO {
    private final String userId;
    private final String name;
    private final int todayTransferCount;
    private final double todayTransferUsdAmount;
    private final List<TradeHistoryDTO> history;

    public TradeHistResponseDTO(String status, Integer resultCode, String resultMsg,
                                String userId, String name, int todayTransferCount,
                                double todayTransferUsdAmount, List<TradeHistoryDTO> history) {
        super(status, resultCode, resultMsg);
        this.userId = userId;
        this.name = name;
        this.todayTransferCount = todayTransferCount;
        this.todayTransferUsdAmount = todayTransferUsdAmount;
        this.history = history;
    }

    public static TradeHistResponseDTO of(String status, Integer resultCode, String resultMsg,
                                          String userId, String name, int todayTransferCount,
                                          double todayTransferUsdAmount, List<TradeHistoryDTO> history) {
        return new TradeHistResponseDTO(status, resultCode, resultMsg,
                userId, name, todayTransferCount, todayTransferUsdAmount, history);
    }
}