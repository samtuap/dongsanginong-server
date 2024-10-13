package org.samtuap.inong.domain.farm.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageListener {

    private final CacheManager cacheManager;

    @KafkaListener(topics = "update-like", groupId = "product-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenUpdateLike(Long memberId) {
        clearCache(memberId);
    }

    private void clearCache(Long memberId) {
        cacheManager.getCache("FarmList").evict(memberId);
    }

}
