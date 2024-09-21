package org.samtuap.inong.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.product.entity.PackageProductImage;
import org.samtuap.inong.domain.product.repository.PackageProductImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PackageProductImageService {

    private final PackageProductImageRepository packageProductImageRepository;
    private final S3Service s3Service;

    // 이미지 저장 로직 분리
    @Transactional
    public void saveImages(PackageProduct packageProduct, List<String> imageKeys) {
        for (String imageKey : imageKeys) {
            // Presigned URL 생성
            String presignedUrl = s3Service.generatePreSignedUrl(imageKey);

            // PackageProductImage 엔티티 생성 및 저장
            PackageProductImage productImage = PackageProductImage.builder()
                    .imageUrl(presignedUrl)
                    .packageProduct(packageProduct)
                    .build();

            packageProductImageRepository.save(productImage);
        }
    }
}