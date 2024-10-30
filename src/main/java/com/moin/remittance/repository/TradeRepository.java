package com.moin.remittance.repository;

import com.moin.remittance.domain.entity.Trade.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface TradeRepository extends JpaRepository<TradeEntity, Long> {
    TradeEntity findByTranscationId(Long transcationId);
}
