package org.samtuap.inong.domain.member.jwt.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.exception.TokenException;
import org.samtuap.inong.domain.member.jwt.domain.JwtToken;
import org.samtuap.inong.domain.member.jwt.domain.JwtValidator;
import org.samtuap.inong.domain.member.jwt.securities.JwtProvider;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

import static org.samtuap.inong.common.exceptionType.TokenExceptionType.*;

@RequiredArgsConstructor
@Service
public class JwtService {
    private final JwtProvider jwtProvider;
    private final JwtValidator jwtValidator;
    private final RedisTemplate<String, Object> redisTemplate;

    public JwtToken issueToken(Long memberId, String role) {
        String accessToken = jwtProvider.createToken(memberId, role);
        String refreshToken = jwtProvider.createRefreshToken(memberId, role);

        saveRefreshTokenToRedis(memberId, refreshToken);

        return JwtToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Jwt 재발급
    public JwtToken reissueToken(String requestRefreshToken, Long memberId, String role) {
        // 리프레시 토큰 검사
        String refreshToken = getRefreshTokenFromRedis(memberId);

        // 1. requestRefreshToken 이 null 이거나 empty
        if(requestRefreshToken == null || requestRefreshToken.isEmpty()) {
            throw new TokenException(NO_REFRESH_TOKEN);
        }

        // 2. 요청 헤더로 들어온 것과 같은가
        if(!refreshToken.equals(requestRefreshToken)) {
            throw new TokenException(INVALID_REFRESH_TOKEN);
        }

        // 3. 파싱했을 때 유효한가
        if(!jwtValidator.isValidToken(refreshToken)) {
            throw new TokenException(INVALID_TOKEN);
        }


        return issueToken(memberId, role);
    }

    private void saveRefreshTokenToRedis(Long memberId, String refreshToken) {
        redisTemplate.opsForValue().set(memberId.toString(), refreshToken, Duration.ofDays(14));
    }

    private String getRefreshTokenFromRedis(Long memberId) {
        return (String) redisTemplate.opsForValue().get(memberId.toString());
    }

    private Long getMemberIdFromRefreshToken(String refreshToken) {
        Long memberId = null;
        try {
            memberId = Long.parseLong((String) Objects.requireNonNull(redisTemplate.opsForValue().get(refreshToken)));
        } catch(Exception e) {
            throw new TokenException(INVALID_REFRESH_TOKEN);
        }
        return memberId;
    }

    public void deleteRefreshToken(Long memberId) {
        redisTemplate.delete(memberId.toString());
    }


}

