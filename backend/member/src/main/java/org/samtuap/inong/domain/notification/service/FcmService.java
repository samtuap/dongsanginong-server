package org.samtuap.inong.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.repository.MemberRepository;
import org.samtuap.inong.domain.notification.dto.FcmTokenSaveRequest;
import org.samtuap.inong.domain.notification.dto.NotificationIssueRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.samtuap.inong.common.exceptionType.NotificationExceptionType.FCM_SEND_FAIL;
import static org.samtuap.inong.common.exceptionType.NotificationExceptionType.FCM_TOKEN_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class FcmService {
    private final MemberRepository memberRepository;
    @Transactional
    public void saveFcmToken(Long memberId, FcmTokenSaveRequest fcmTokenSaveRequest) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        member.updateFcmToken(fcmTokenSaveRequest.fcmToken());

    }
    public void sendTestMessage(Long myId) {
        Member member = memberRepository.findByIdOrThrow(myId);
        String token = member.getFcmToken();

        if(token == null || token.isEmpty()) {
            throw new BaseCustomException(FCM_TOKEN_NOT_FOUND);
        }

        Message message = Message.builder()
                .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(WebpushNotification.builder()
                                .setTitle("알림 제목")
                                .setBody("알림 내용")
                                .build())
                        .build())
                .setToken(token)
                .build();

        try {
            FirebaseMessaging.getInstance().sendAsync(message);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new BaseCustomException(FCM_SEND_FAIL);
        }

    }

    public void issueNotice(NotificationIssueRequest notiRequest) {
        List<Long> targets = notiRequest.targets();
        for (Long memberId : targets) {
            issueMessage(memberId, notiRequest.title(), notiRequest.content());
        }
    }

    private void issueMessage(Long memberId, String title, String content) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        String token = member.getFcmToken();

        if(token == null || token.isEmpty()) {
            throw new BaseCustomException(FCM_TOKEN_NOT_FOUND);
        }

        Message message = Message.builder()
                .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(WebpushNotification.builder()
                                .setTitle(title)
                                .setBody(content)
                                .build())
                        .build())
                .setToken(token)
                .build();

        try {
            FirebaseMessaging.getInstance().sendAsync(message);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new BaseCustomException(FCM_SEND_FAIL);
        }
    }
}
