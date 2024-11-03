package com.moin.remittance.service.transfer.impl;

import com.moin.remittance.domain.dto.exchange.ExchangeRateDTO;
import com.moin.remittance.domain.dto.trade.TradeDTO;
import com.moin.remittance.domain.dto.transfer.QuoteReqDTO;
import com.moin.remittance.domain.dto.transfer.QuoteRespDTO;
import com.moin.remittance.domain.entity.Trade.TradeEntity;
import com.moin.remittance.domain.entity.member.MemberEntity;
import com.moin.remittance.exception.NegativeNumberException;
import com.moin.remittance.repository.MemberRepository;
import com.moin.remittance.service.exchange.ExchangeService;
import com.moin.remittance.service.trade.TradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceImplTest {

    @Mock
    private ExchangeService exchangeService;

    @Mock
    private TradeService tradeService;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private TransferServicelmpl transferService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("송금액이 양수가 아닌 경우 NegativeNumberException 발생")
    void calculateQuoteThrowsNegativeNumberException() {
        QuoteReqDTO dto = new QuoteReqDTO();
        dto.setAmount(-1000);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        assertThrows(NegativeNumberException.class, () -> {
            transferService.calculateQuote(dto);
        });
    }

    @Test
    @DisplayName("정상 송금액에 따른 계산 검증")
    void calculateQuoteSuccess() {
        QuoteReqDTO dto = new QuoteReqDTO();
        dto.setAmount(1000);
        dto.setTargetCurrency("USD");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testUser");

        ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO();
        exchangeRateDTO.setBasePrice(1.2);
        HashMap<String, ExchangeRateDTO> exchangeRateInfoHashMap = new HashMap<>();
        exchangeRateInfoHashMap.put("USD", exchangeRateDTO);
        when(exchangeService.fetchExchangeRate("USD")).thenReturn(exchangeRateInfoHashMap);

        MemberEntity memberEntity = spy(MemberEntity.builder()
                .userId("testUser")
                .build());
        when(memberRepository.findByUserId("testUser")).thenReturn(memberEntity);

        // Create TradeEntity object using builder
        TradeEntity tradeEntity = TradeEntity.builder()
                .transcationId(1L)
                .exchangeRate(1.2)
                .targetAmount(800.0)
                .requestedDate(OffsetDateTime.now())
                .sourceAmount(1000L)  // 추가한 필드들
                .fee(10.0)
                .usdExchangeRate(1.2)
                .usdAmount(833.33)
                .targetCurrency("USD")
                .transaction_by(memberEntity)
                .build();

        System.out.println("TradeEntity: " + tradeEntity.toString());


        when(tradeService.saveTrade(any(TradeDTO.class), eq(memberEntity))).thenReturn(tradeEntity);

        QuoteRespDTO response = transferService.calculateQuote(dto);

        assertNotNull(response);
        assertEquals("1", response.getQuoteId());
        assertEquals(BigDecimal.valueOf(1.2), response.getExchangeRate());
        assertTrue(response.getExpireTime().compareTo(OffsetDateTime.now().plusMinutes(10).toString()) < 0);
        assertEquals(BigDecimal.valueOf(800.0), response.getTargetAmount());

        verify(exchangeService, times(1)).fetchExchangeRate("USD");
        verify(memberRepository, times(1)).findByUserId("testUser");
        verify(tradeService, times(1)).saveTrade(any(TradeDTO.class), eq(memberEntity));
    }
}