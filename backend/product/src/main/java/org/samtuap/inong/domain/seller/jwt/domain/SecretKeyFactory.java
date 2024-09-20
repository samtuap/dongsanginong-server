package org.samtuap.inong.domain.seller.jwt.domain;

import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@RequiredArgsConstructor
@Component
public class SecretKeyFactory {
    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    public SecretKey createSecretKey() {
        byte[] keyBytes = secretKey.getBytes();

        return Keys.hmacShaKeyFor(keyBytes);
    }

}
