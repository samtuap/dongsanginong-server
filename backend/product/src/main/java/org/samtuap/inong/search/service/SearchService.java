package org.samtuap.inong.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.product.repository.PackageProductRepository;
import org.samtuap.inong.search.document.FarmDocument;
import org.samtuap.inong.search.document.PackageProductDocument;
import org.samtuap.inong.search.dto.BaseResponse;
import org.samtuap.inong.search.repository.FarmSearchRepository;
import org.samtuap.inong.search.repository.PackageProductSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final FarmRepository farmRepository;
    private final FarmSearchRepository farmSearchRepository;
    private final PackageProductRepository packageProductRepository;
    private final PackageProductSearchRepository packageProductSearchRepository;
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    /**
     * farm에서 검색
     */
    public BaseResponse<FarmDocument> searchFarms(String word1, String word2) {
        elasticsearchOperations.indexOps(FarmDocument.class).refresh();

        SearchHits<FarmDocument> searchHits = farmSearchRepository.findByFarmNameContainingOrFarmIntroContaining(word1, word2);
        return new BaseResponse<>(searchHits.getTotalHits(), searchHits.getSearchHits());
    }

    /**
     * package에서 검색
     */
    public BaseResponse<PackageProductDocument> searchPackageProduct(String word) {
        elasticsearchOperations.indexOps(FarmDocument.class).refresh();

        SearchHits<PackageProductDocument> searchHits = packageProductSearchRepository.findByPackageNameContaining(word);
        return new BaseResponse<>(searchHits.getTotalHits(), searchHits.getSearchHits());
    }

    /**
     * farm 엔티티 > farmDocument로 인덱싱
     */
    public void indexAllFarms() {
        List<Farm> farms = farmRepository.findAll();

        // farmdocument로 변환 후 인덱싱
        List<FarmDocument> farmDocuments = farms.stream()
                .map(farm -> FarmDocument.builder()
                        .id(farm.getId().toString())
                        .sellerId(farm.getSellerId())
                        .farmName(farm.getFarmName())
                        .bannerImageUrl(farm.getBannerImageUrl())
                        .profileImageUrl(farm.getProfileImageUrl())
                        .farmIntro(farm.getFarmIntro())
                        .favoriteCount(farm.getFavoriteCount())
                        .orderCount(farm.getOrderCount())
                        .build())
                .collect(Collectors.toList());

        log.info("farmDocuments : {}", farmDocuments);
        // elastic search에 저장(=인덱싱)
        farmSearchRepository.saveAll(farmDocuments);
    }

    /**
     * productPackage > productPackage document로 인덱싱
     */
    public void indexAllProducts() {
        List<PackageProduct> products = packageProductRepository.findAll();

        List<PackageProductDocument> packageProductDocuments = products.stream()
                .map(product -> PackageProductDocument.builder()
                        .id(product.getId().toString())
                        .packageName(product.getPackageName())
                        .price(product.getPrice())
                        .delivery_cycle(product.getDelivery_cycle())
                        .farmId(product.getFarm().getId().toString())
                        .build())
                .toList();

        log.info("packageProductDocuments : {}", packageProductDocuments);
        packageProductSearchRepository.saveAll(packageProductDocuments);
    }

}
