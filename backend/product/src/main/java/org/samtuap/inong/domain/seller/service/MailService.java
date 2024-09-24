package org.samtuap.inong.domain.seller.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.seller.dto.SellerSignUpRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final RedisTool redisUtil;


    public void authEmail(String email, Object dto) {
        Random random = new Random();
        String authKey = String.valueOf(random.nextInt(888888) + 111111);

        sendAuthEmail(email, authKey);

        redisUtil.setExpire(email, authKey, 60 * 5L);
        redisUtil.setExpire(email+":data", dto, 60 * 5L);
    }

    private void sendAuthEmail(String email, String authKey) {

        String subject = "[동상이농] Authorization";
        String message = "인증번호는 " + authKey + "입니다.";

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(message, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyAuthCode(String email, String code) {
        String storedCode = redisUtil.getValue(email, String.class);
        return code.equals(storedCode);
    }


    public SellerSignUpRequest getUserData(String email, Class<SellerSignUpRequest> sellerSignUpRequestClass) {
        String key = email + ":data";
        SellerSignUpRequest data = redisUtil.getValue(key, sellerSignUpRequestClass);
        log.info("Fetched user data from Redis: {}", data);
        return data;
    }


}
