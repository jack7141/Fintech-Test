package com.moin.remittance.core.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moin.remittance.core.config.JwtConfig;
import com.moin.remittance.core.jwt.provider.AuthUserDetailsProvider;
import com.moin.remittance.core.jwt.provider.JwtTokenProvider;
import com.moin.remittance.domain.dto.response.HttpResponseBody;
import com.moin.remittance.exception.NotFoundMemberException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Iterator;

@Slf4j
@Component
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final JwtConfig jwtConfig;

    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   JwtTokenProvider jwtTokenProvider,
                                   JwtConfig jwtConfig, ObjectMapper objectMapper
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtConfig = jwtConfig;
        this.objectMapper = objectMapper;

        super.setAuthenticationManager(authenticationManager);

        log.info("authenticationManager" + authenticationManager);
        setFilterProcessesUrl(jwtConfig.AUTH_LOGIN_END_POINT);
    }

    /**
     * 🔐 인증 시도 메소드
     * /login 엔드포인트로 요청 => 필터 => 인증 시도
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String userId = obtainUsername(request);
        String password = obtainPassword(request);

        log.info("userId: " + userId);
        log.info("password: " + password);

        // 인증정보 객체 생성: 스프링 시큐리티에서 username, password 검증하기 위해서는 token에 담아야 함
        Authentication authentication = new UsernamePasswordAuthenticationToken(userId, password);
        log.info("authentication: " + authentication);

        // 사용자 인증 여부를 판단하는 객체
        authentication = authenticationManager.authenticate(authentication);

        log.info("인증 여부: " + authentication.isAuthenticated());

        if (!authentication.isAuthenticated()) {
            log.info("인증 실패!!!");
            throw new NotFoundMemberException("BAD_NOT_MATCH_MEMBER");
        }

        return authentication;
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain,
            Authentication authentication
    ) throws IOException, ServletException {
        AuthUserDetailsProvider member = (AuthUserDetailsProvider) authentication.getPrincipal();

        Iterator<? extends GrantedAuthority> iterator = member.getAuthorities().iterator();
        String idType = iterator.next().getAuthority();
        String userId = member.getUsername();

        String jwt = jwtTokenProvider.createAuthorizationToken(userId, idType, 60 * 60 * 1000);

        log.info("jwt: " + jwt);


        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(member, null);

        SecurityContextHolder.getContext().setAuthentication(authToken);

        // JSON 으로 변환하여 응답 본문에 작성
        response.addHeader(jwtConfig.AUTH_TOKEN_HEADER, jwtConfig.AUTH_TOKEN_PREFIX + jwt);
        response.setStatus(200); // Setting status code directly
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(HttpResponseBody.builder()
                        .statusCode(200) // Using direct status code
                        .message("Login successful") // Direct message
                        .codeName("SUCCESS") // Direct code name
                        .token(jwt)
                        .build()
                )
        );
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.sendError(400, "Invalid credentials"); // Direct error message
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(HttpResponseBody.builder()
                        .statusCode(400) // Using direct error code
                        .message("Invalid credentials") // Direct error message
                        .codeName("ERROR") // Direct code name
                        .build()
                )
        );
    }
}

