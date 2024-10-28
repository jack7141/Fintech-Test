package com.moin.remittance.domain.dto.transfer;
import lombok.Data;

@Data
public class QuoteReqDTO {
    private int amount; // Sending amount (positive integer)
    private String targetCurrency; // Currency to receive
}