package org.samtuap.inong.domain.seller.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Random;

import static org.samtuap.inong.common.exceptionType.SellerExceptionType.EMAIL_SEND_FAILED;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final RedisTool redisUtil;


    public void authEmail(String email) {
        Random random = new Random();
        String authKey = String.valueOf(random.nextInt(888888) + 111111);

        sendAuthEmail(email, authKey);
        redisUtil.setExpire(email, authKey, 60 * 5L);
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
            throw new BaseCustomException(EMAIL_SEND_FAILED);
        }
    }
}
