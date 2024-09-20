package org.samtuap.inong.domain.seller.jwt.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.seller.jwt.domain.JwtToken;
import org.samtuap.inong.domain.seller.jwt.domain.JwtValidator;
import org.samtuap.inong.domain.seller.securities.JwtProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class JwtService {
    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;
    private final RedisTemplate<String, Object> redisTemplate;

    public JwtToken issueToken(Long sellerId) {
        String accessToken = jwtProvider.createToken(sellerId);
        String refreshToken = jwtProvider.createRefreshToken(sellerId);

        saveRefreshTokenToRedis(sellerId, refreshToken);

        return JwtToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 재발급
    public JwtToken reissueToken(String requestRefreshToken, Long sellerId) {
        // 리프레시 토큰 검사
        String refreshToken = getRefreshTokenFromRedis(sellerId);

        // 1. requestRefreshToken이 null이거나 empty
        if(requestRefreshToken == null || requestRefreshToken.isEmpty()) {
            throw new RuntimeException("NO REFRESH TOKEN");
        }

        // 2. 요청 헤더로 들어온 것과 같은가
        if(!refreshToken.equals(requestRefreshToken)) {
            throw new RuntimeException("NO REFRESH TOKEN");
        }

        // 3. 파싱했을 때 유효한가
        if(!jwtValidator.isValidToken(refreshToken)) {
            throw new RuntimeException("NO REFRESH TOKEN");
        }


        return issueToken(sellerId);
    }

    private void saveRefreshTokenToRedis(Long sellerId, String refreshToken) {
        redisTemplate.opsForValue().set("sellerRT:"+ sellerId.toString(), refreshToken, Duration.ofDays(14));
    }

    private String getRefreshTokenFromRedis(Long sellerId) {
        return (String) redisTemplate.opsForValue().get(sellerId.toString());
    }

    private Long getSellerIdFromRefreshToken(String refreshToken) {
        Long sellerId = null;
        try {
            sellerId = Long.parseLong((String) Objects.requireNonNull(redisTemplate.opsForValue().get(refreshToken)));
        } catch(Exception e) {
            throw new RuntimeException("NO REFRESH TOKEN");
        }
        return sellerId;
    }

    public void deleteRefreshToken(Long sellerId) {
        redisTemplate.delete("sellerRT:" + sellerId.toString());
    }


}

