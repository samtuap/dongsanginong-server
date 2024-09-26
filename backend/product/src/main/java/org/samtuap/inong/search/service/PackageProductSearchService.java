package org.samtuap.inong.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.*;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.opensearch.client.transport.endpoints.BooleanResponse;
import org.samtuap.inong.search.document.PackageProductDocument;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackageProductSearchService {

    private final OpenSearchClient openSearchClient;
    private static final String INDEX_NAME = "package_product";

    @PostConstruct
    public void init() {
        try {
            // 인덱스가 존재하는지 확인 후 존재하지 않을 때만 생성
            ExistsRequest existsRequest = ExistsRequest.of(e -> e.index(INDEX_NAME));
            BooleanResponse existsResponse = openSearchClient.indices().exists(existsRequest);

            if (!existsResponse.value()) {
                createIndex();
            } else {
                log.info("인덱스가 이미 존재합니다: {}", INDEX_NAME);
            }
        } catch (Exception e) {
            log.error("Error initializing PackageProductSearchService: ", e);
        }
    }

    // 인덱스 생성
    public void createIndex() {
        try {
            CreateIndexRequest request = CreateIndexRequest.of(builder -> builder.index(INDEX_NAME));
            openSearchClient.indices().create(request);
            log.info("index 생성 : {}", INDEX_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // openSearch에 인덱싱 (=저장)
    public void indexProductDocument(PackageProductDocument packageProductDocument) {
        try {
            IndexRequest<PackageProductDocument> indexRequest = IndexRequest.of(builder ->
                    builder.index(INDEX_NAME)
                            .id(packageProductDocument.getId())
                            .document(packageProductDocument)
            );
            IndexResponse response = openSearchClient.index(indexRequest);
            log.info("open search에 인덱싱 : {}", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 검색 쿼리
    public List<PackageProductDocument> searchProducts(String keyword) {
        List<PackageProductDocument> products = new ArrayList<>();
        try {
            // open search에서 검색
            SearchRequest request = SearchRequest.of(searchRequest ->
                    searchRequest.index(INDEX_NAME)
                            .query(query -> query
                                    .bool(bool -> bool
                                            .should(should -> should
                                                    .wildcard(wildcard -> wildcard
                                                            .field("packageName")
                                                            .value("*" + keyword + "*")
                                                    )
                                            )
                                            .should(should -> should
                                                    .wildcard(wildcard -> wildcard
                                                            .field("productDescription")
                                                            .value("*" + keyword + "*")
                                                    )
                                            )
                                    )
                            )
            );
            // open search에서 응답 가져오기
            SearchResponse<PackageProductDocument> response = openSearchClient.search(request, PackageProductDocument.class);
            // hit : 검색 쿼리와 일치하는 개별 검색 결과 => 즉, 검색 결과 반환
            List<Hit<PackageProductDocument>> hits = response.hits().hits();
            for (Hit<PackageProductDocument> hit : hits) {
                products.add(hit.source());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    // 삭제
    public void deleteProduct(String id) {
        try {
            DeleteRequest deleteRequest = DeleteRequest.of(builder ->
                    builder.index(INDEX_NAME)
                            .id(id));
            DeleteResponse response = openSearchClient.delete(deleteRequest);
            log.info("삭제 응답 : {}", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 수정
    public void updateProduct(PackageProductDocument packageProductDocument) {
        try {
            // index_name & id와 일치한 document 찾아서 > 받아온 값으로 업데이트
            UpdateRequest<PackageProductDocument, Object> request = UpdateRequest.of(builder -> builder
                    .index(INDEX_NAME)
                    .id(packageProductDocument.getId())
                    .doc(packageProductDocument));
            UpdateResponse<PackageProductDocument> response = openSearchClient.update(request, PackageProductDocument.class);
            log.info("수정 응답 : {}", response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}