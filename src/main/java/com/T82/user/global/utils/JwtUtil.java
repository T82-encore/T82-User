package com.T82.user.global.utils;

import com.T82.user.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {
    private final Long expiration;
    private final SecretKey secret;

    //환경변수 설정한 값들 변수에 주입작업
    public JwtUtil(
            @Value("${jwt.expiration}") Long expiration,
            @Value("${jwt.secret}") String secret) {
        this.expiration = expiration;
        this.secret = Keys.hmacShaKeyFor(secret.getBytes());;
    }

    //JWT 토큰 생성작업
    public String generateToken(User user) {
        return Jwts.builder()
                .claim("id", user.getUserId())
                .claim("email", user.getEmail())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.secret)
                .compact();
    }

    //JWT 토큰 정보 보기 작업
    public TokenInfo parseToken(String token) {
    Claims payload = (Claims) Jwts.parser()
                .verifyWith(secret)
                .build()
                .parse(token)
                .getPayload();
        return TokenInfo.fromClaims(payload);
    }

    //JWT 토큰 만료됐는지 검증하는 작업
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parse(token);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}
