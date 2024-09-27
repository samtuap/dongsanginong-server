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

    @Transactional
    public void saveImages(PackageProduct packageProduct, List<String> imageUrls) {
        for (String imageUrl : imageUrls) {

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
        }
    }
}