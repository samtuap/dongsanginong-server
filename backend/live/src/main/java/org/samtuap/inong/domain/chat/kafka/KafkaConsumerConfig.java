package org.samtuap.inong.domain.chat.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.samtuap.inong.domain.chat.dto.ChatMessageRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

public class KafkaConsumerConfig {

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, ChatMessageRequest> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ChatMessageRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ChatMessageRequest> consumerFactory() {
        // JsonDeserializer 설정
        JsonDeserializer<ChatMessageRequest> jsonDeserializer = new JsonDeserializer<>(ChatMessageRequest.class);
        jsonDeserializer.setRemoveTypeHeaders(false);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeMapperForKey(true);

        return new DefaultKafkaConsumerFactory<>(consumerConfigurations(),
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(jsonDeserializer));
    }

    @Bean
    public Map<String, Object> consumerConfigurations() {
        Map<String, Object> configurations = new HashMap<>();
        configurations.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.KAFKA_BROKER);
        configurations.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.GROUP_ID);
        configurations.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        configurations.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configurations.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return configurations;
    }
}
