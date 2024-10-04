package org.samtuap.inong.config;

import io.openvidu.java.client.OpenVidu;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenViduConfig {

    @Value("${openvidu.url}")
    private String openviduUrl;

    @Value("${openvidu.secret}")
    private String openviduSecret;

    @Bean
    public OpenVidu openVidu() {
        // OpenVidu 객체를 생성하여 빈으로 등록
        return new OpenVidu(openviduUrl, openviduSecret);
    }
}
