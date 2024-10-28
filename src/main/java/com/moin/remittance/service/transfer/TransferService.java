package com.moin.remittance.service.transfer;

import com.moin.remittance.domain.dto.member.MemberDTO;
import com.moin.remittance.domain.dto.transfer.QuoteReqDTO;
import com.moin.remittance.domain.dto.transfer.QuoteRespDTO;
import org.springframework.stereotype.Service;

@Service
public interface TransferService {
    public QuoteRespDTO calculateQuote(QuoteReqDTO dto);
}
