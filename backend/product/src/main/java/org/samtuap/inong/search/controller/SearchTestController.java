package org.samtuap.inong.search.controller;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.search.service.SearchTestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search-test")
@RequiredArgsConstructor
public class SearchTestController {

    private final SearchTestService searchTestService;

    // RDBMS 성능 테스트
    @GetMapping("/rdbms")
    public ResponseEntity<String> testRDBMSSearch(@RequestParam String keyword) {
        long startTime = System.currentTimeMillis();
        var farms = searchTestService.searchFarmRDBMS(keyword);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        return ResponseEntity.ok("RDBMS 검색 시간: " + duration + "ms, 결과 수: " + farms.size());
    }

    // OpenSearch 성능 테스트
    @GetMapping("/opensearch")
    public ResponseEntity<String> testOpenSearch(@RequestParam String keyword) {
        long startTime = System.currentTimeMillis();
        var farms = searchTestService.searchFarmOpenSearch(keyword);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        return ResponseEntity.ok("OpenSearch 검색 시간: " + duration + "ms, 결과 수: " + farms.size());
    }
}