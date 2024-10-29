package com.moin.remittance.service.exchange;

import com.moin.remittance.domain.dto.exchange.ExchangeRateDTO;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public interface ExchangeService {
    HashMap<String, ExchangeRateDTO> fetchExchangeRate(String codes);
}
