package com.moin.remittance.service.trade;

import com.moin.remittance.domain.dto.trade.TradeDTO;
import com.moin.remittance.domain.dto.trade.TradeRespDTO;
import com.moin.remittance.domain.entity.Trade.TradeEntity;
import com.moin.remittance.domain.entity.member.MemberEntity;
import org.springframework.stereotype.Service;

@Service
public interface TradeService {

    public TradeEntity saveTrade(TradeDTO dto, MemberEntity currentUser);

    public TradeEntity acceptQuoteTransaction(Long id);

    public TradeRespDTO getUserTrade(String userId);
}
