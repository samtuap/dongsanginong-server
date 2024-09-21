package org.samtuap.inong.domain.product.api;

import org.samtuap.inong.domain.product.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @GetMapping("/generate-presigned-url")
    public String generatePresignedUrl(@RequestParam String objectKey) {
        return s3Service.generatePreSignedUrl(objectKey); // S3Service로 요청 위임
    }
}
