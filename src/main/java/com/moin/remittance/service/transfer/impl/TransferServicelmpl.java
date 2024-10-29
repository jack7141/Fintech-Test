package com.moin.remittance.service.transfer.impl;

import com.moin.remittance.domain.dto.transfer.QuoteReqDTO;
import com.moin.remittance.domain.dto.transfer.QuoteRespDTO;
import com.moin.remittance.service.transfer.TransferService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

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
    private static final String EXCHANGE_RATE_API_URL = "https://crix-api-cdn.upbit.com/v1/forex/recent";

    public QuoteRespDTO calculateQuote(QuoteReqDTO dto) {
        BigDecimal sendingAmount = BigDecimal.valueOf(dto.getAmount());
        String targetCurrency = dto.getTargetCurrency();

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

        // Fetch exchange rate (assume getExchangeRate() method retrieves rate)
        BigDecimal exchangeRate = getExchangeRate(targetCurrency);

        // Calculate target amount in the receiving currency
        BigDecimal targetAmount = netSendingAmount.divide(exchangeRate, 2, RoundingMode.HALF_UP);

        // Create response DTO
        QuoteRespDTO response = QuoteRespDTO.builder()
                .quoteId(generateQuoteId())
                .exchangeRate(exchangeRate)
                .expireTime(LocalDateTime.now().plus(10, ChronoUnit.MINUTES).toString())
                .targetAmount(targetAmount)
                .build();

        return response;
    }

    private BigDecimal getExchangeRate(String targetCurrency) {
        BigDecimal rate = BigDecimal.valueOf(9.0798);
        return rate; // Placeholder
    }

    private String generateQuoteId() {
        return "Q" + System.currentTimeMillis();
    }
}
