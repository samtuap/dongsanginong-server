package org.samtuap.inong.domain.chat.websocket;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final SecretKeyFactory secretKeyFactory;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.token.access_expiration_time}")
    private Long accessExpirationTime;

    @Value("${jwt.token.refresh_expiration_time}")
    private Long refreshExpirationTime;

    public String createToken(Long memberId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        Date now = new Date();

        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessExpirationTime))
                .setSubject(Long.toString(memberId))
                .setClaims(claims)
                .signWith(secretKeyFactory.createSecretKey())
                .compact();
    }

    public String createRefreshToken(Long memberId, String role) {
        Date now = new Date();
        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshExpirationTime))
                .signWith(secretKeyFactory.createSecretKey())
                .claim("role", role)
                .compact();

        return refreshToken;
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}