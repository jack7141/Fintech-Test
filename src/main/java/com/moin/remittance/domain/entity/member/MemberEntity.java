package com.moin.remittance.domain.entity.member;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Component
@Entity
@Table(name = "member")
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(nullable = false)
    private String role;

    @Column(name = "id_type", nullable = false)
    private String idType;

    @Column(name = "id_value", nullable = false)
    private String idValue;

    @Builder
    public MemberEntity(String userId, String name, String password, String role, String idType, String idValue) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.role = role != null ? role : "REG_NO";
        this.idType = idType;
        this.idValue = idValue;
    }

    @PrePersist
    @PreUpdate
    private void validate() {
        if (userId == null || !userId.contains("@") || !userId.contains(".")) {
            throw new IllegalArgumentException("Invalid email format for userId");
        }
    }
}
