package com.moin.remittance.service.trade.Impl;

import com.moin.remittance.domain.dto.trade.TradeDTO;
import com.moin.remittance.domain.dto.trade.TradeHistoryDTO;
import com.moin.remittance.domain.dto.trade.TradeRespDTO;
import com.moin.remittance.domain.entity.Trade.TradeEntity;
import com.moin.remittance.domain.entity.member.MemberEntity;
import com.moin.remittance.exception.MemberNotFoundException;
import com.moin.remittance.exception.QuoteExpiredException;
import com.moin.remittance.repository.MemberRepository;
import com.moin.remittance.repository.TradeRepository;
import com.moin.remittance.service.trade.TradeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final MemberRepository memberRepository;


    public TradeEntity saveTrade(TradeDTO dto, MemberEntity currentUser) {
        return tradeRepository.save(dto.toEntity(dto, currentUser));
    }

    public TradeEntity acceptQuoteTransaction(Long id) {
        TradeEntity tradeEntity = tradeRepository.findByTranscationId(id);
        if (tradeEntity == null) {
            throw new QuoteExpiredException("찾을 수 없는 송금장입니다.");
        }

        OffsetDateTime currentTime = OffsetDateTime.now();
        Duration duration = Duration.between(tradeEntity.getRequestedDate(), currentTime);
        if (duration.toMinutes() > 10) {
            throw new QuoteExpiredException("송금 접수 시간이 만료되었습니다.");
        }
        return tradeEntity;
    }

    public TradeRespDTO getUserTrade(String userId) {
        MemberEntity memberEntity = memberRepository.findByUserId(userId);
        if (memberEntity == null) {
            throw new MemberNotFoundException("존재하지 않는 회원입니다.");
        }
        List<TradeHistoryDTO> transferHistory = memberEntity.getTrades().stream().map(trade ->
                TradeHistoryDTO.builder()
                        .sourceAmount(trade.getSourceAmount())
                        .fee(trade.getFee())
                        .usdExchangeRate(trade.getUsdExchangeRate())
                        .usdAmount(trade.getUsdAmount())
                        .targetCurrency(trade.getTargetCurrency())
                        .exchangeRate(trade.getExchangeRate())
                        .targetAmount(trade.getTargetAmount())
                        .requestedDate(trade.getRequestedDate().toLocalDateTime())
                        .build()
        ).collect(Collectors.toList());

        return TradeRespDTO.builder()
                .userId(memberEntity.getUserId())
                .name(memberEntity.getName())
                .todayTransferCount((int) transferHistory.stream()
                        .filter(trade -> trade.getRequestedDate().toLocalDate().isEqual(LocalDate.now()))
                        .count()) // 오늘 송금 횟수
                .todayTransferUsdAmount(transferHistory.stream()
                        .filter(trade -> trade.getRequestedDate().toLocalDate().isEqual(LocalDate.now()))
                        .mapToDouble(TradeHistoryDTO::getUsdAmount)
                        .sum()) // 오늘 송금 금액
                .history(transferHistory)
                .build();
    }
}
