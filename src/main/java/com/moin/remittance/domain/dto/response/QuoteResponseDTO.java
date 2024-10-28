package com.moin.remittance.domain.dto.response;

import lombok.Getter;

@Getter
public class QuoteResponseDTO <T> extends ResponseDTO{
    private final T quote;

    private QuoteResponseDTO(T data, String message){
        super("success",0,message);
        this.quote = data;
    }

    public static <T> QuoteResponseDTO<T> of(T data, String message) {
        return new QuoteResponseDTO<>(data, message);
    }
}
