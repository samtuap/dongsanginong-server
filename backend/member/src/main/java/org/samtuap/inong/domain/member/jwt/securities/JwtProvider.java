package org.samtuap.inong.domain.member.jwt.securities;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.samtuap.inong.domain.member.jwt.domain.SecretKeyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtProvider {

    private final SecretKeyFactory secretKeyFactory;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.token.access_expiration_time}")
    private Long accessExpirationTime;

    @Value("${jwt.token.refresh_expiration_time}")
    private Long refreshExpirationTime;

    public String createToken(Long memberId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", memberId);

        Date now = new Date();

        return Jwts.builder()
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessExpirationTime))
                .claims(claims)
                .signWith(secretKeyFactory.createSecretKey())
                .compact();
    }

    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        String refreshToken = Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpirationTime))
                .signWith(secretKeyFactory.createSecretKey())
                .compact();

        // Redis에 리프레시 토큰 저장 (memberId 를 key 로 사용)
        String redisKey = "RT:" + memberId;
        redisTemplate.opsForValue().set(redisKey, refreshToken, refreshExpirationTime, TimeUnit.MILLISECONDS);

        return refreshToken;
    }
}