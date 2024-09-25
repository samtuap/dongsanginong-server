package org.samtuap.inong.domain.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.OrderFeign;
import org.samtuap.inong.domain.product.dto.*;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.product.entity.PackageProductImage;
import org.samtuap.inong.domain.product.repository.PackageProductImageRepository;
import org.samtuap.inong.domain.product.repository.PackageProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.*;

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
        Farm farm = farmRepository.findById(request.farmId())
                .orElseThrow(() -> new BaseCustomException(FARM_NOT_FOUND));

        PackageProduct packageProduct = PackageProductCreateRequest.toEntity(farm, request);
        PackageProduct savedPackageProduct = packageProductRepository.save(packageProduct);

        List<String> preSignedUrls = request.imageUrls();
        List<String> imageUrls = new ArrayList<>();  // 실제 URL을 저장할 리스트

        // for 문으로 쿼리 파라미터 제거 후 실제 URL 리스트 생성
        for (String preSignedUrl : preSignedUrls) {
            String imageUrl = extractImageUrl(preSignedUrl);  // 쿼리 파라미터 제거
            imageUrls.add(imageUrl);
        }

        packageProductImageService.saveImages(savedPackageProduct, imageUrls);

        return PackageProductCreateResponse.fromEntity(savedPackageProduct, imageUrls);
    }

    // 쿼리 파라미터 제거 로직
    private String extractImageUrl(String presignedUrl) {
        int queryIndex = presignedUrl.indexOf('?');
        if (queryIndex != -1) {
            return presignedUrl.substring(0, queryIndex);  // 쿼리 파라미터 제거
        }
        return presignedUrl;  // 쿼리 파라미터가 없으면 그대로 반환
    }

    @Transactional
    public Page<SellerPackageListGetResponse> getSellerPackages(Long sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PackageProduct> packageProductPage = packageProductRepository.findBySellerId(sellerId, pageable);
        return SellerPackageListGetResponse.fromEntities(packageProductPage);
    }

    @Transactional
    public void deletePackage(Long sellerId, Long packageId) {
        PackageProduct packageProduct = packageProductRepository.findByIdOrThrow(packageId);
        if (!packageProduct.getFarm().getSellerId().equals(sellerId)) {
            throw new BaseCustomException(UNAUTHORIZED_ACTION);
        }
        packageProductRepository.delete(packageProduct);
    }

    @Transactional
    public void updatePackageProduct(Long sellerId, Long packageId, PackageProductUpdateRequest request) {
        // 상품 조회 및 검증
        PackageProduct packageProduct = packageProductRepository.findById(packageId)
                .orElseThrow(() -> new BaseCustomException(PRODUCT_NOT_FOUND));

        if (!packageProduct.getFarm().getSellerId().equals(sellerId)) {
            throw new BaseCustomException(UNAUTHORIZED_ACTION);
        }

        // 상품 정보 및 이미지 수정
        request.updatePackageProduct(packageProduct, packageProductImageService);

        // 수정된 상품 정보 저장
        packageProductRepository.save(packageProduct);
    }

    public List<PackageProductResponse> getProductInfoList(List<Long> ids) {
        List<PackageProduct> packageProducts = packageProductRepository.findAllById(ids);
        return packageProducts.stream()
                .map(p -> PackageProductResponse.fromEntity(p, new ArrayList<>())).toList();
    }
  
    @Transactional
    public List<PackageProductSubsResponse> getProductSubsList(List<Long> subscriptionIds) {
        List<PackageProduct> subsPackageProductList = packageProductRepository.findAllByIds(subscriptionIds);
        return subsPackageProductList.stream()
                .map(packageProduct -> {
                    String imageUrl = packageProductImageRepository.findFirstByPackageProduct(packageProduct).getImageUrl();
                    Farm farm = farmRepository.findByIdOrThrow(packageProduct.getFarm().getId());
                    return PackageProductSubsResponse.fromEntity(packageProduct, imageUrl, farm);
                })
                .collect(Collectors.toList());
    }
}
