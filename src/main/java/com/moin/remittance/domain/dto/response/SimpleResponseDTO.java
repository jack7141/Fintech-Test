package com.moin.remittance.domain.dto.response;

import lombok.Getter;

@Getter
public class SimpleResponseDTO {
    private int resultCode;
    private String resultMsg;

    // Constructor
    public SimpleResponseDTO(int resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    // Getters and Setters (or you can use Lombok annotations)
    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}