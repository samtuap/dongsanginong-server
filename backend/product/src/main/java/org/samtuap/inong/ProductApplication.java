package org.samtuap.inong;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.util.TimeZone;

@EnableCaching
@SpringBootApplication
public class ProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApplication.class, args);
	}

	@PostConstruct
	public void init() {
		// timezone KST로 설정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
}
