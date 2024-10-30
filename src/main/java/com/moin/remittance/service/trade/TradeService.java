package com.moin.remittance.service.trade;

import com.moin.remittance.domain.dto.trade.TradeDTO;
import com.moin.remittance.domain.entity.Trade.TradeEntity;
import com.moin.remittance.domain.entity.member.MemberEntity;
import com.moin.remittance.repository.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeService {

    private final TradeRepository tradeRepository;

    public TradeEntity saveTrade(TradeDTO dto, MemberEntity currentUser) {
        return tradeRepository.save(dto.toEntity(dto, currentUser));
    }
}
