package org.samtuap.inong.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "org.samtuap.inong.common.client")
public class FeignConfig {
}
