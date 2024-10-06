package org.samtuap.inong.search.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.samtuap.inong.search.document.FarmDocument;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchTestService {

    private final FarmRepository farmRepository;
    private final FarmSearchService farmSearchService;

    // RDBMS 검색
    public List<Farm> searchFarmRDBMS(String keyword) {
        return farmRepository.findByFarmNameContainingOrFarmIntroContaining(keyword, keyword);
    }

    // OpenSearch 검색
    public List<FarmDocument> searchFarmOpenSearch(String keyword) {
        return farmSearchService.searchFarms(keyword);
    }
}