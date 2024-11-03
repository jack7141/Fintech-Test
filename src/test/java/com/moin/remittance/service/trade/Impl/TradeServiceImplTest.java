package com.moin.remittance.service.trade.impl;

import com.moin.remittance.domain.dto.trade.TradeDTO;
import com.moin.remittance.domain.dto.trade.TradeRespDTO;
import com.moin.remittance.domain.entity.Trade.TradeEntity;
import com.moin.remittance.domain.entity.member.MemberEntity;
import com.moin.remittance.exception.MemberNotFoundException;
import com.moin.remittance.exception.QuoteExpiredException;
import com.moin.remittance.repository.MemberRepository;
import com.moin.remittance.repository.TradeRepository;
import com.moin.remittance.service.trade.TradeService;
import com.moin.remittance.service.trade.Impl.TradeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
class TradeServiceImplTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private TradeServiceImpl tradeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("거래 저장 - saveTrade()")
    void saveTradeTest() {
        MemberEntity member = MemberEntity.builder()
                .userId("testUser")
                .build();
        TradeDTO tradeDTO = TradeDTO.builder()
                .sourceAmount(1000L) // long 타입으로 수정
                .usdAmount(100.0)
                .build();
        TradeEntity tradeEntity = tradeDTO.toEntity(tradeDTO, member);

        when(tradeRepository.save(any(TradeEntity.class))).thenReturn(tradeEntity);

        TradeEntity savedTrade = tradeService.saveTrade(tradeDTO, member);

        assertNotNull(savedTrade);
        assertEquals(1000L, savedTrade.getSourceAmount());
        verify(tradeRepository, times(1)).save(any(TradeEntity.class));
    }

    @Test
    @DisplayName("거래 수락 - acceptQuoteTransaction()")
    void acceptQuoteTransactionTest() {
        TradeEntity tradeEntity = TradeEntity.builder()
                .transcationId(1L)
                .requestedDate(OffsetDateTime.now().minusMinutes(5))
                .build();

        when(tradeRepository.findByTranscationId(1L)).thenReturn(tradeEntity);

        TradeEntity acceptedTrade = tradeService.acceptQuoteTransaction(1L);

        assertNotNull(acceptedTrade);
        assertEquals(1L, acceptedTrade.getTranscationId());
        verify(tradeRepository, times(1)).findByTranscationId(1L);
    }


    @Test
    @DisplayName("송금 만료 예외 테스트 - acceptQuoteTransaction()")
    void acceptQuoteTransactionQuoteExpiredTest() {
        TradeEntity tradeEntity = TradeEntity.builder()
                .transcationId(1L)
                .requestedDate(OffsetDateTime.now().minusMinutes(15))
                .build();

        when(tradeRepository.findByTranscationId(1L)).thenReturn(tradeEntity);

        assertThrows(QuoteExpiredException.class, () -> {
            tradeService.acceptQuoteTransaction(1L);
        });

        verify(tradeRepository, times(1)).findByTranscationId(1L);
    }


    @Test
    @DisplayName("존재하지 않는 송금장 예외 테스트 - acceptQuoteTransaction()")
    void acceptQuoteTransactionNotFoundTest() {
        when(tradeRepository.findByTranscationId(1L)).thenReturn(null);

        assertThrows(QuoteExpiredException.class, () -> {
            tradeService.acceptQuoteTransaction(1L);
        });

        verify(tradeRepository, times(1)).findByTranscationId(1L);
    }

    @Test
    @DisplayName("사용자 거래 조회 - getUserTrade()")
    void getUserTradeTest() {
        MemberEntity memberEntity = spy(MemberEntity.builder()
                .userId("testUser")
                .name("Test User")
                .build());

        List<TradeEntity> trades = new ArrayList<>();
        trades.add(TradeEntity.builder()
                .sourceAmount(1000L) // long 타입으로 수정
                .fee(10.0)
                .usdExchangeRate(1.2)
                .usdAmount(100.0)
                .targetCurrency("KRW")
                .exchangeRate(1100.0)
                .targetAmount(110000.0)
                .requestedDate(OffsetDateTime.now())
                .build());

        // 실제로 거래 리스트를 추가
        doReturn(trades).when(memberEntity).getTrades();

        when(memberRepository.findByUserId("testUser")).thenReturn(memberEntity);

        TradeRespDTO tradeRespDTO = tradeService.getUserTrade("testUser");

        assertNotNull(tradeRespDTO);
        assertEquals("testUser", tradeRespDTO.getUserId());
        assertEquals(1, tradeRespDTO.getTodayTransferCount());
        verify(memberRepository, times(1)).findByUserId("testUser");
    }


    @Test
    @DisplayName("존재하지 않는 사용자 예외 테스트 - getUserTrade()")
    void getUserTradeNotFoundTest() {
        when(memberRepository.findByUserId("nonExistentUser")).thenReturn(null);

        assertThrows(MemberNotFoundException.class, () -> {
            tradeService.getUserTrade("nonExistentUser");
        });

        verify(memberRepository, times(1)).findByUserId("nonExistentUser");
    }

}