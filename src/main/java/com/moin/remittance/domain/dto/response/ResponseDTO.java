package com.moin.remittance.domain.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class ResponseDTO {
    private final String status;
    private final Integer resultCode;
    private final String resultMsg;


    public static ResponseDTO of(String status, int resultCode, String resultMsg) {
        return new ResponseDTO(status, resultCode, resultMsg);
    }
}
