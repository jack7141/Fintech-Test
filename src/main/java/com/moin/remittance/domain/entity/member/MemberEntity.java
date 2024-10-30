package com.moin.remittance.domain.entity.member;

import com.moin.remittance.domain.entity.Trade.TradeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
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


    @OneToMany(mappedBy = "transaction_by", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TradeEntity> trades = new ArrayList<>();



    public MemberEntity toEntity(PasswordEncoder passwordEncoder) {
        MemberEntity member = MemberEntity.builder()
                .userId(this.userId)
                .name(this.name)
                .role(this.role != null ? this.role : "REG_NO")
                .password(this.password != null ? passwordEncoder.encode(this.password) : null)
                .build();

        return member;
    }

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
