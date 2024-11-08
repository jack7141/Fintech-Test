package com.moin.remittance.core.jwt.filter;

import com.moin.remittance.core.config.JwtConfig;
import com.moin.remittance.core.jwt.provider.AuthUserDetailsProvider;
import com.moin.remittance.core.jwt.provider.JwtTokenProvider;
import com.moin.remittance.domain.entity.member.MemberEntity;
import com.moin.remittance.exception.UnAuthorizationJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtConfig jwtConfig;

    /**
     * JWT 요청 필터
     * - JWT 유효성 검사
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(jwtConfig.AUTH_TOKEN_HEADER);

        // 엔드포인트가 "/api/v2/user/login" 일때만 걸림 => 토큰이 없는 경우
        if (header == null || header.isEmpty() || !header.startsWith(jwtConfig.AUTH_TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        /*
         * 이 부분부터는 헤더가 있는 경우 이므로 토큰만 추출 "Bearer " + JWT
         * */
        String token = header.split(jwtConfig.AUTH_TOKEN_PREFIX)[1];


        if (jwtTokenProvider.isExpiredJWT(token)) {
            filterChain.doFilter(request, response);
            throw new UnAuthorizationJwtException("UNAUTHORIZED_EXPIRED_JWT");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        AuthUserDetailsProvider member = (AuthUserDetailsProvider) authentication.getPrincipal();

        String userId = member.getUsername();
        Iterator<? extends GrantedAuthority> iterator = member.getAuthorities().iterator();
        String idType = iterator.next().getAuthority();


        AuthUserDetailsProvider user = new AuthUserDetailsProvider(
                MemberEntity.builder()
                        .userId(userId)
                        .idType(idType)
                        .build()
        );


        /* 인증된 유저
         * setAuthentication 파라미터 타입: Authentication
         * */
        SecurityContextHolder
                .getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())
                );

        filterChain.doFilter(request, response);
    }
}
