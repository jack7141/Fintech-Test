package com.moin.remittance.core.jwt.application;

import com.moin.remittance.core.jwt.provider.AuthUserDetailsProvider;
import com.moin.remittance.domain.entity.member.MemberEntity;
import com.moin.remittance.exception.NotFoundMemberException;
import com.moin.remittance.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * Spring Security => 유저 정보 조회 => 인증 처리
     * */
    @Override
    public UserDetails loadUserByUsername(String userId) throws InternalAuthenticationServiceException {
        MemberEntity member = memberRepository.findByUserId(userId);

        /*
         * @Exception DB에 일치하는 유저 없는 경우
         * */
        if(member == null) {
            throw new NotFoundMemberException("일치하는 회원이 없습니다.");
        }

        return new AuthUserDetailsProvider(member);
    }
}
