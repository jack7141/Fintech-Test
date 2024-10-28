package com.moin.remittance.service.meber;

import com.moin.remittance.domain.dto.member.MemberDTO;
import com.moin.remittance.domain.dto.member.MemberLoginReqDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MemberService {
    public List<MemberDTO> getMembers();
    public MemberDTO saveUser(MemberDTO dto);
}
