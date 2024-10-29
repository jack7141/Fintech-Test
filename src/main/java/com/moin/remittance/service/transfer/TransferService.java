package com.moin.remittance.service.transfer;

import com.moin.remittance.domain.dto.member.MemberDTO;
import com.moin.remittance.domain.dto.transfer.QuoteReqDTO;
import com.moin.remittance.domain.dto.transfer.QuoteRespDTO;
import org.springframework.stereotype.Service;

@Service
public interface TransferService {
    /**
     *
     * @param dto
     * 수수료 = 보내는 금액 * 수수료율 + 고정수수료
     *  3050 = 10000 * 0.005 + 3000
     *  받는 금액 = 10000 - 3050
     */
    public QuoteRespDTO calculateQuote(QuoteReqDTO dto);
}
