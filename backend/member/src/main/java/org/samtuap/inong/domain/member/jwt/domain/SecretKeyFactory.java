package org.samtuap.inong.domain.member.jwt.domain;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static java.util.Base64.getEncoder;

@RequiredArgsConstructor
@Component
public class SecretKeyFactory {
    @Value("${jwt.secretKey}")
    private String secretKey;

    public SecretKey createSecretKey() {
        byte[] keyBytes = secretKey.getBytes();

        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

}
