package org.samtuap.inong.search.repository;

import org.samtuap.inong.search.document.PackageProductDocument;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageProductSearchRepository extends ElasticsearchRepository<PackageProductDocument, Long> {
    SearchHits<PackageProductDocument> findByPackageNameContaining(String word);
}
