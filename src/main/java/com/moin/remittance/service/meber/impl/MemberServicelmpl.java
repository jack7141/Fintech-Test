package com.moin.remittance.service.meber.impl;

import com.moin.remittance.domain.dto.member.MemberDTO;
import com.moin.remittance.domain.dto.member.MemberLoginReqDTO;
import com.moin.remittance.domain.entity.member.MemberEntity;
import com.moin.remittance.exception.DuplicateUserIdException;
import com.moin.remittance.exception.InValidPatternTypeException;
import com.moin.remittance.repository.MemberRepository;
import com.moin.remittance.service.meber.MemberService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServicelmpl  implements MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;

    private static final Logger logger = Logger.getLogger(MemberService.class.getName());

    public List<MemberDTO> getMembers() {
//        logger.info("Fetching all members from the database.");
        List<MemberEntity> members = memberRepository.findAll();

//        logger.info("Number of members found: " + members.size());
        members.forEach(member -> logger.info(member.toString()));
        return members.stream()
                .map(MemberDTO::of)
                .collect(Collectors.toList());
    }
    public MemberDTO saveUser(MemberDTO member) {
        // 1. id type에 따른 id value 정규표현식 체크
        // 개인회원: 주민등록번호
        String RESIDENT_NUMBER_REGEX = "\\d{6}-\\d{7}";

        // 법인 회원: 사업자 등록번호
        String BUSINESS_NUMBER_REGEX = "\\d{3}-\\d{2}-\\d{5}";

        Pattern residentPattern = Pattern.compile(RESIDENT_NUMBER_REGEX);
        Pattern businessPattern = Pattern.compile(BUSINESS_NUMBER_REGEX);

        Matcher targetMatcher;
        switch (member.getIdType().toUpperCase()) {
            // type 매칭 -> 패턴 체크
            case "REG_NO":
                targetMatcher = residentPattern.matcher(member.getIdValue());
                if (!targetMatcher.matches()) {
                    throw new InValidPatternTypeException("BAD_INDIVIDUAL_MEMBER_INVALID_ID_VALUE");
                }
                break;
            case "BUSINESS_NO":
                targetMatcher = businessPattern.matcher(member.getIdValue());
                if (!targetMatcher.matches()) {
                    throw new InValidPatternTypeException("BAD_CORPORATION_MEMBER_INVALID_ID_VALUE");
                }
                break;
            default:
                // @valid에서 체킹 하지만 혹시 모를 예외 상황 대비
                throw new InValidPatternTypeException("BAD_REQUEST_BODY_INVALID_ERROR");
        }

        // 2. 회원 중복 조회
        boolean isExistingUserId = memberRepository.existsByUserId(member.getUserId());

        if (isExistingUserId) {
            throw new DuplicateUserIdException("BAD_DUPLICATE_USERID_INVALID_USERID");
        }

        // 3. 비밀번호, 주민등록번호 or 사업자등록번호 암호화해서 저장
        MemberEntity savedMember = memberRepository.saveAndFlush(
                MemberEntity.builder()
                        .userId(member.getUserId())
                        .password(bCryptPasswordEncoder.encode(member.getPassword()))
                        .name(member.getName())
                        .idType(member.getIdType())
                        .idValue(bCryptPasswordEncoder.encode(member.getIdValue()))
                        .build()
        );
        return MemberDTO.of(savedMember); // 저장된 엔티티를 DTO로 변환하여 반환
    }
}
