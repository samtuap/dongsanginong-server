package org.samtuap.inong.domain.seller.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.seller.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailVerificationService {

    private final SellerRepository sellerRepository;

    private final RedisTool redisTool;

    private final EmailTool emailTool;
    private static final String AUTH_CODE_PREFIX = "AuthCode ";

    @Value("${spring.mail.properties.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    public void sendCodeToEmail(String toEmail) {
        //이메일 중복 검사
        if(sellerRepository.existsByEmail(toEmail)) throw new IllegalStateException("중복된 이메일 입니다.");

        //인증코드 생성, 저장 및 이메일 전송
        String title = "유저 이메일 인증 번호";
        String authCode = this.createCode();
        // 이메일 인증 요청 시 인증 번호 Redis에 저장
        redisTool.setValues(AUTH_CODE_PREFIX + toEmail,
                authCode, Duration.ofMillis(authCodeExpirationMillis));
        emailTool.sendEmail(toEmail, title, authCode);
    }


    public boolean verifyCode(String email, String authCode) {
        String redisAuthCode = redisTool.getValues(AUTH_CODE_PREFIX + email);

        return redisTool.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);
    }

    private String createCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("암호 생성 ERROR");
        }
    }
}