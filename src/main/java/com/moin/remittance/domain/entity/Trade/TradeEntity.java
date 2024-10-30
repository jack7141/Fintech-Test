package com.moin.remittance.domain.entity.Trade;

import com.moin.remittance.domain.entity.member.MemberEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@Component
@Entity
@Table(name = "trade")
public class TradeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transcation_id")
    private Long transcationId;

    // 송금 할 금액(원화)
    @Column(name = "source_amount", nullable = false)
    private long sourceAmount;

    // 적용된 수수료
    @Column(name = "fee", nullable = false)
    private double fee;

    // USD 환율(base price)
    @Column(name = "usd_exchange_rate", nullable = false)
    private double usdExchangeRate;

    // USD 송금액
    @Column(name = "usd_amount", nullable = false)
    private double usdAmount;

    // 받는 환율 정보
    @Column(name = "target_currency", nullable = false)
    private String targetCurrency;

    // targetCurrency가 미국이면 미국 환율 일본이면 일본 환율
    @Column(name = "exchange_rate", nullable = false)
    private double exchangeRate;

    // 받는 금액
    @Column(name = "target_amount", nullable = false)
    private double targetAmount;


    @Column(name = "requested_date", nullable = false)
    private OffsetDateTime requestedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private MemberEntity transaction_by;

    public TradeEntity toEntity() {
        TradeEntity tradeEntity = TradeEntity.builder().build();
        return new TradeEntity();
    }
}
