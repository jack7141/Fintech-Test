package com.moin.remittance.domain.dto.trade;


import com.moin.remittance.domain.entity.Trade.TradeEntity;
import com.moin.remittance.domain.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeDTO {
    private Long transcationId;

    private long sourceAmount;

    private double fee;

    private double usdExchangeRate;

    private double usdAmount;

    private String targetCurrency;

    private double exchangeRate;

    private double targetAmount;


    public TradeEntity toEntity (TradeDTO dto, MemberEntity currentUser) {
        return TradeEntity.builder()
                .sourceAmount(dto.getSourceAmount())
                .fee(dto.getFee())
                .usdExchangeRate(dto.getUsdExchangeRate())
                .usdAmount(dto.getUsdAmount())
                .targetCurrency(dto.getTargetCurrency())
                .exchangeRate(dto.getExchangeRate())
                .targetAmount(dto.getTargetAmount())
                .requestedDate(OffsetDateTime.now())
                .transaction_by(currentUser)
                .build();
    }

}
