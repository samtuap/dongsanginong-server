package org.samtuap.inong.domain.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.OrderFeign;
import org.samtuap.inong.domain.product.dto.PackageProductResponse;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.samtuap.inong.domain.product.dto.PackageProductCreateRequest;
import org.samtuap.inong.domain.product.dto.PackageProductCreateResponse;
import org.samtuap.inong.domain.product.dto.TopPackageGetResponse;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.product.entity.PackageProductImage;
import org.samtuap.inong.domain.product.repository.PackageProductImageRepository;
import org.samtuap.inong.domain.product.repository.PackageProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.FARM_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class PackageProductService {
    private final PackageProductRepository packageProductRepository;
    private final PackageProductImageRepository packageProductImageRepository;
    private final OrderFeign orderFeign;
    private final FarmRepository farmRepository;
    private final PackageProductImageService packageProductImageService;
    public List<TopPackageGetResponse> getTopPackages() {
        List<Long> topPackages = orderFeign.getTopPackages();
        List<PackageProduct> products = packageProductRepository.findAllByIds(topPackages);

        return products.stream()
                .map(TopPackageGetResponse::fromEntity)
                .sorted(Comparator.comparingInt(product -> topPackages.indexOf(product.id())))
                .collect(Collectors.toList());
    }

    public PackageProductResponse getProductInfo(Long packageProductId) {
        PackageProduct packageProduct = packageProductRepository.findByIdOrThrow(packageProductId);
        List<PackageProductImage> packageProductImage = packageProductImageRepository.findAllByPackageProduct(packageProduct);
        return PackageProductResponse.fromEntity(packageProduct, packageProductImage);
    }

    @Transactional
    public PackageProductCreateResponse createPackageProduct(PackageProductCreateRequest request) {
        // 농장 조회 후 사용
        Farm farm = farmRepository.findById(request.farmId())
                .orElseThrow(() -> new BaseCustomException(FARM_NOT_FOUND));

        // 상품 엔티티 생성 및 저장
        PackageProduct packageProduct = PackageProductCreateRequest.toEntity(farm, request);
        PackageProduct savedPackageProduct = packageProductRepository.save(packageProduct);

        List<String> imageUrls = request.imageUrls();

        // 이미지 저장 로직 호출
        packageProductImageService.saveImages(savedPackageProduct, imageUrls);

        // 저장된 엔티티를 DTO로 반환
        return PackageProductCreateResponse.fromEntity(savedPackageProduct, imageUrls);
    }
}
