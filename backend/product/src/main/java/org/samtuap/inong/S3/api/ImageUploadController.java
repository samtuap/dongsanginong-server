package org.samtuap.inong.S3.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.S3.FileService;
import org.samtuap.inong.S3.dto.PresignedRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ImageUploadController {
    private final FileService fileService;
    @PostMapping("/api/upload/presigned-url")
    public String getPresignedUrl(@RequestBody PresignedRequest presignedRequest) {
        return fileService.getPreSignedUrl(presignedRequest.prefix(), presignedRequest.url());
    }
}