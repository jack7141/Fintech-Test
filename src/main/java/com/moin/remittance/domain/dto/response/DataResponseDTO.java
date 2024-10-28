package com.moin.remittance.domain.dto.response;

import lombok.Getter;

@Getter
public class DataResponseDTO<T> extends ResponseDTO {
    private final T data;

    private DataResponseDTO(T data){
        super("success",0,"success");
        this.data = data;
    }

    private DataResponseDTO(T data, String message){
        super("success",0,message);
        this.data=data;
    }
    public static <T> DataResponseDTO<T> of(T data) {
        return new DataResponseDTO<>(data);
    }

    public static <T> DataResponseDTO<T> of(T data, String message) {
        return new DataResponseDTO<>(data, message);
    }

    public static <T> DataResponseDTO<T> empty() {
        return new DataResponseDTO<>(null);
    }
}