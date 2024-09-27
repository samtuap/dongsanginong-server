package org.samtuap.inong.search.controller;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.search.document.FarmDocument;
import org.samtuap.inong.search.service.FarmSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/es/farms")
@RequiredArgsConstructor
public class FarmSearchController {

    private final FarmSearchService farmSearchService;

    // openSearch에 인덱싱 (=저장)
    @PostMapping("/index")
    public ResponseEntity<String> indexFarm(@RequestBody FarmDocument farmDocument) {
        farmSearchService.indexFarmDocument(farmDocument);
        return new ResponseEntity<>("farm document 인덱싱 완료", HttpStatus.OK);
    }

    // 검색
    @GetMapping("/search")
    public ResponseEntity<List<FarmDocument>> searchFarms(@RequestParam String keyword) {
        List<FarmDocument> farms = farmSearchService.searchFarms(keyword);
        return new ResponseEntity<>(farms, HttpStatus.OK);
    }

    // 수정
    @PutMapping("/update")
    public ResponseEntity<String> updateFarm(@RequestBody FarmDocument farmDocument) {
        farmSearchService.updateFarm(farmDocument);
        return new ResponseEntity<>("수정 완료", HttpStatus.OK);
    }
}
