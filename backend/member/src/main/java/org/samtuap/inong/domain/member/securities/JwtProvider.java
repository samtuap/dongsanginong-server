package org.samtuap.inong.domain.member.securities;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Log4j2
public class JwtProvider {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.token.access-expiration-time}")
    private Long accessExpirationTime;

    @Value("${jwt.token.refresh-expiration-time}")
    private Long refreshExpirationTime;

    public String createToken(Long memberId) {
        Date now = new Date();
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessExpirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes());

        return jwtBuilder.compact();
    }

    public String createRefreshToken(Long memberId) {
        Date now = new Date();
        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExpirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();

        // Redis에 리프레시 토큰 저장 (memberId 를 key 로 사용)
        String redisKey = "RT:" + memberId;
        redisTemplate.opsForValue().set(redisKey, refreshToken, refreshExpirationTime, TimeUnit.MILLISECONDS);

        return refreshToken;
    }
}