package org.samtuap.inong.domain.product.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.S3.ImageService;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.product.entity.PackageProductImage;
import org.samtuap.inong.domain.product.repository.PackageProductImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PackageProductImageService {

    private final PackageProductImageRepository packageProductImageRepository;
    private final ImageService imageService;

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
    public List<String> findAllImageUrlsByPackageProduct(PackageProduct packageProduct) {
        List<PackageProductImage> productImages = packageProductImageRepository.findAllByPackageProduct(packageProduct);
        List<String> imageUrls = new ArrayList<>();
        for (PackageProductImage productImage : productImages) {
            imageUrls.add(productImage.getImageUrl());
        }
        return imageUrls;
    }

    @Transactional
    public void deleteImages(PackageProduct packageProduct, List<String> imageUrls) {
        // DB에서 이미지 삭제
        for (String imageUrl : imageUrls) {
            packageProductImageRepository.deleteByPackageProductAndImageUrl(packageProduct, imageUrl);
        }
        imageService.deleteImagesFromS3(imageUrls);
    }
}