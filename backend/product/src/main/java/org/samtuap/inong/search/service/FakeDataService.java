package org.samtuap.inong.search.service;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.samtuap.inong.search.document.FarmDocument;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * rdbms와 opensesarch의 성능 비교를 위한 가짜 데이터 추가하는 코드
 */
@Service
@RequiredArgsConstructor
public class FakeDataService {

    private final FarmRepository farmRepository;
    private final OpenSearchClient openSearchClient;
    private static final String INDEX_NAME = "farm";

//    @PostConstruct
    public void init() {
        createFakeData();
    }

    public void createFakeData() {
        Faker faker = new Faker();

        // RDBMS에 가짜 데이터 생성 및 저장
        List<Farm> farms = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            farms.add(Farm.builder()
                    .sellerId(1L)
                    .farmName(faker.company().name())
                    .bannerImageUrl("https://dongsanginong-bucket.s3.ap-northeast-2.amazonaws.com/Farm/76a8d608-1204-4c33-bda1-5add61205e71%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-10-05%20174008.png")
                    .profileImageUrl("https://dongsanginong-bucket.s3.ap-northeast-2.amazonaws.com/Farm/76a8d608-1204-4c33-bda1-5add61205e71%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202024-10-05%20174008.png")
                    .farmIntro(faker.lorem().sentence())
                    .favoriteCount(0L)
                    .orderCount(0L)
                    .build());
        }
        farmRepository.saveAll(farms);

        // opensearch에 가짜 데이터 생성 및 저장 => 한 번에 100개씩 배치를 나누어 인덱싱
        int batchSize = 100;
        List<FarmDocument> farmDocuments = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            FarmDocument farmDocument = FarmDocument.builder()
                    .id(faker.number().digits(10))
                    .farmName(faker.company().name())
                    .farmIntro(faker.lorem().sentence())
                    .build();
            farmDocuments.add(farmDocument);

            // batchSize만큼 데이터가 모이면 인덱싱 수행
            if (farmDocuments.size() == batchSize) {
                indexBulkData(farmDocuments);
                farmDocuments.clear(); // 인덱싱 후 리스트 초기화
            }
        }
        // 남은 데이터 인덱싱
        if (!farmDocuments.isEmpty()) {
            indexBulkData(farmDocuments);
        }
    }

    private void indexBulkData(List<FarmDocument> farmDocuments) {
        BulkRequest.Builder bulkRequestBuilder = new BulkRequest.Builder();
        for (FarmDocument farmDocument : farmDocuments) {
            bulkRequestBuilder.operations(op -> op.index(idx -> idx
                    .index(INDEX_NAME)
                    .id(farmDocument.getId())
                    .document(farmDocument)
            ));
        }
        // Bulk 인덱싱 요청 실행
        try {
            BulkResponse bulkResponse = openSearchClient.bulk(bulkRequestBuilder.build());
            if (bulkResponse.errors()) {
                bulkResponse.items().forEach(item -> {
                    if (item.error() != null) {
                        System.out.println("에러 발생: " + item.error().reason());
                    }
                });
                throw new RuntimeException("OpenSearch 인덱싱 중 오류 발생");
            } else {
                System.out.println("인덱싱 성공: " + bulkResponse.items().size() + "건");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
