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

        BigDecimal commissionRate;
        BigDecimal fixedFee;

        return null;
    }
}
