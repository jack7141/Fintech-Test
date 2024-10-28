package com.moin.remittance.domain.entity.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum roleEntity {
    USER("REG_NO","개인회원"),
    ADMIN("BUSINESS_NO", "법인회원");

    private final String key;
    private final String title;

}
