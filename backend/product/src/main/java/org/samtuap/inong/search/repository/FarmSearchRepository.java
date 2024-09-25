package org.samtuap.inong.search.repository;

import org.samtuap.inong.search.document.FarmDocument;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FarmSearchRepository extends ElasticsearchRepository<FarmDocument, Long> {
   SearchHits<FarmDocument> findByFarmNameContainingOrFarmIntroContaining(String word1, String word2);
}
