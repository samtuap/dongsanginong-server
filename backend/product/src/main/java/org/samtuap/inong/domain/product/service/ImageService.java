package org.samtuap.inong.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Service s3Service;

    // Presigned URL에서 실제 URL 추출 (쿼리 파라미터 제거)
    public String extractImageUrl(String presignedUrl) {
        int queryIndex = presignedUrl.indexOf('?');
        if (queryIndex != -1) {
            return presignedUrl.substring(0, queryIndex);
        }
        return presignedUrl;
    }

    // 여러 개의 Presigned URL에서 실제 URL 추출
    public List<String> extractImageUrls(List<String> presignedUrls) {
        List<String> imageUrls = new ArrayList<>();
        for (String presignedUrl : presignedUrls) {
            imageUrls.add(extractImageUrl(presignedUrl));
        }
        return imageUrls;
    }

    // 이미지 삭제 로직
    public void deleteImagesFromS3(List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            s3Service.deleteFileFromS3(imageUrl);
        }
    }
}
