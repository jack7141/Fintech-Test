package com.moin.remittance.service.transfer.impl;

import com.moin.remittance.domain.dto.exchange.ExchangeRateDTO;
import com.moin.remittance.domain.dto.transfer.QuoteReqDTO;
import com.moin.remittance.domain.dto.transfer.QuoteRespDTO;
import com.moin.remittance.exception.NegativeNumberException;
import com.moin.remittance.service.exchange.ExchangeService;
import com.moin.remittance.service.transfer.TransferService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

@Service
@Transactional
@RequiredArgsConstructor
public class TransferServicelmpl implements TransferService {


    private static final BigDecimal USD_FIXED_FEE_BELOW_MILLION = new BigDecimal("1000");
    private static final BigDecimal USD_FIXED_FEE_ABOVE_MILLION = new BigDecimal("3000");
    private static final BigDecimal JPY_FIXED_FEE = new BigDecimal("3000");
    private static final BigDecimal USD_COMMISSION_BELOW_MILLION = new BigDecimal("0.002");
    private static final BigDecimal USD_COMMISSION_ABOVE_MILLION = new BigDecimal("0.001");
    private static final BigDecimal JPY_COMMISSION = new BigDecimal("0.005");

    private final ExchangeService exchangeService;

    public QuoteRespDTO calculateQuote(QuoteReqDTO dto) {
        BigDecimal sendingAmount = BigDecimal.valueOf(dto.getAmount());

        if (sendingAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeNumberException("송금액을 양수로 입력 해주세요.");
        }

        String targetCurrency = dto.getTargetCurrency();

        // 2. 외부API 호출로 환율 정보 응답 데이터 받기
        HashMap<String, ExchangeRateDTO> exchangeRateInfoHashMap = exchangeService.fetchExchangeRate(targetCurrency);// 환율 정보 DTO
        ExchangeRateDTO exchangeRateDTO = exchangeRateInfoHashMap.values().iterator().next();
        double exchangeRate = exchangeRateDTO.getBasePrice();

        // Define variables for commission rate and fixed fee
        BigDecimal commissionRate;
        BigDecimal fixedFee;

        // Determine commission rate and fixed fee based on currency and sending amount
        if (targetCurrency.equals("USD")) {
            if (sendingAmount.compareTo(BigDecimal.valueOf(1_000_000)) < 0) {
                commissionRate = USD_COMMISSION_BELOW_MILLION;
                fixedFee = USD_FIXED_FEE_BELOW_MILLION;
            } else {
                commissionRate = USD_COMMISSION_ABOVE_MILLION;
                fixedFee = USD_FIXED_FEE_ABOVE_MILLION;
            }
        } else if (targetCurrency.equals("JPY")) {
            commissionRate = JPY_COMMISSION;
            fixedFee = JPY_FIXED_FEE;
        } else {
            throw new IllegalArgumentException("Unsupported target currency: " + targetCurrency);
        }

        // 수수료 계산
        // 수수료 = 보내는금액(amount) * 수수료율 + 고정수수료
        BigDecimal commission = sendingAmount.multiply(commissionRate);
        BigDecimal totalFee = commission.add(fixedFee);

        // 받는 금액 계산
        // 받는 금액 = (보내는 금액 - 수수료) / 환율
        BigDecimal netSendingAmount = sendingAmount.subtract(totalFee);

        // Calculate target amount in the receiving currency
        BigDecimal targetAmount = netSendingAmount.divide(BigDecimal.valueOf(exchangeRate), 2, BigDecimal.ROUND_HALF_UP);

        // Create response DTO
        QuoteRespDTO response = QuoteRespDTO.builder()
                .quoteId(generateQuoteId())
                .exchangeRate(BigDecimal.valueOf(exchangeRate))
                .expireTime(LocalDateTime.now().plus(10, ChronoUnit.MINUTES).toString())
                .targetAmount(targetAmount)
                .build();

        return response;
    }


    private String generateQuoteId() {
        return "Q" + System.currentTimeMillis();
    }
}
