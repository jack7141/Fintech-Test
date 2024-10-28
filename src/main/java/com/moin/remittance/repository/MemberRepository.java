package com.moin.remittance.repository;

import com.moin.remittance.domain.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository  extends JpaRepository<MemberEntity, Long> {
    boolean existsByUserId(String userId);

    boolean existsByUserIdAndPassword(String userId, String password);

    MemberEntity findByUserId(String userId);

}
