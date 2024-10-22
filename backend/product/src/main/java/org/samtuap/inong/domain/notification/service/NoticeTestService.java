package org.samtuap.inong.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.client.MemberFeign;
import org.samtuap.inong.domain.farmNotice.dto.KafkaNotificationRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NoticeTestService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MemberFeign memberFeign;


    // 동기
    public void noticeSync(Long memberId) {
        KafkaNotificationRequest notification = KafkaNotificationRequest.builder()
                .memberId(memberId)
                .title("비동기 테스트 알림입니다.")
                .content("비동기 테스트 알림입니다.")
                .url("")
                .build();
        memberFeign.sendNotification(memberId, notification);
    }

    // 비동기
    public void noticeAsync(Long memberId) {
        KafkaNotificationRequest notification = KafkaNotificationRequest.builder()
                .memberId(memberId)
                .title("비동기 테스트 알림입니다.")
                .content("비동기 테스트 알림입니다.")
                .url("")
                .build();
        kafkaTemplate.send("member-notification-topic", notification);
    }
}
