package org.samtuap.inong.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.samtuap.inong.search.document.FarmDocument;
import org.samtuap.inong.search.repository.FarmSearchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final FarmRepository farmRepository;
    private final FarmSearchRepository farmSearchRepository;

    public List<FarmDocument> searchFarms(String word1, String word2) {
        return farmSearchRepository.findByFarmNameContainingOrFarmIntroContaining(word1, word2);
    }


    // rdb > elastic search로 데이터 인덱싱
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
}
