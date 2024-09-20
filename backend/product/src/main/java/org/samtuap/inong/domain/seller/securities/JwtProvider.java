package org.samtuap.inong.domain.seller.securities;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.samtuap.inong.domain.seller.jwt.domain.SecretKeyFactory;
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

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    @Value("${JWT_TOKEN_ACCESS_EXPIRATION_TIME}")
    private Long accessExpirationTime;

    @Value("${JWT_TOKEN_REFRESH_EXPIRATION_TIME}")
    private Long refreshExpirationTime;

    public String createToken(Long sellerId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sellerId", sellerId);

        Date now = new Date();

        return Jwts.builder()
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessExpirationTime))
                .claims(claims)
                .signWith(secretKeyFactory.createSecretKey())
                .compact();
    }

    public String createRefreshToken(Long sellerId) {
        Date now = new Date();
        String refreshToken = Jwts.builder()
                .subject(String.valueOf(sellerId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpirationTime))
                .signWith(secretKeyFactory.createSecretKey())
                .compact();

        return refreshToken;
    }
}