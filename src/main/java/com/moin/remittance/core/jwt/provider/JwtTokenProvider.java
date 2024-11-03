package com.moin.remittance.core.jwt.provider;

import com.moin.remittance.core.config.JwtConfig;
import com.moin.remittance.domain.entity.member.MemberEntity;
import com.moin.remittance.exception.UnAuthorizationJwtException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    private final JwtConfig jwtConfig;

    public String createAuthorizationToken(String userId, String idType, long milliSecond) {
        return Jwts.builder()
                .setHeaderParam("type", jwtConfig.AUTH_TOKEN_TYPE)
                .signWith(Keys.hmacShaKeyFor(jwtConfig.SECRET_KEY.getBytes()), SignatureAlgorithm.HS512)
                .setExpiration(new Date(System.currentTimeMillis() + milliSecond))
                .claim("userId", userId)
                .claim("idType", idType)
                .compact();
    }


    public Authentication getAuthentication(String token) {
        try {
            log.info("token: " + token);

            // JWT Parsing
            Jws<Claims> parsedToken = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtConfig.SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(token);

            String userId = parsedToken.getBody().get("userId").toString();
            String idType = parsedToken.getBody().get("idType").toString();

            if (userId == null || userId.isEmpty()) {
                throw new UnAuthorizationJwtException("유효하지 않은 페이로드 회원 데이터");
            }

            AuthUserDetailsProvider user = new AuthUserDetailsProvider(
                    MemberEntity.builder()
                            .userId(userId)
                            .idType(idType)
                            .build()
            );

            return new UsernamePasswordAuthenticationToken(user, null);
        } catch (ExpiredJwtException e) {
            throw new UnAuthorizationJwtException("권한 만료된 JWT 토큰");
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new UnAuthorizationJwtException("유효하지 않은 JWT 토큰");
        }
    }


    public boolean isExpiredJWT (String jwt) {
        try {
            // JWT Parsing
            Jws<Claims> parsedToken = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtConfig.SECRET_KEY.getBytes()))
                    .build()
                    .parseClaimsJws(jwt);
            System.out.println(parsedToken);
            Date exp = parsedToken.getBody().getExpiration();

            return exp.before(new Date());

        } catch (ExpiredJwtException e) {
            throw new UnAuthorizationJwtException("권한 만료된 JWT 토큰");
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new UnAuthorizationJwtException("유효하지 않은 JWT 토큰");
        } catch (NullPointerException e) {
            throw new UnAuthorizationJwtException("존재하지 않는 토큰");
        }
    }
}

