package com.moin.remittance.domain.dto.response;

import lombok.Builder;

@Builder
public record HttpResponseBody<T> (int statusCode, String message, String codeName, String token, T data){
}
