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
    public void saveImages(PackageProduct packageProduct, List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            // PackageProductImage 엔티티 생성 및 저장
            PackageProductImage productImage = PackageProductImage.builder()
                    .imageUrl(imageUrl)
                    .packageProduct(packageProduct)
                    .build();
            packageProductImageRepository.save(productImage);
        }
    }

    @Transactional
    public void deleteImages(PackageProduct packageProduct, List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            // DB에서 이미지 삭제
            packageProductImageRepository.deleteByPackageProductAndImageUrl(packageProduct, imageUrl);

            // S3에서 이미지 삭제 (이미지 URL을 기반으로 S3 객체 삭제)
            s3Service.deleteFileFromS3(imageUrl);
        }
    }
}