package org.samtuap.inong.search.controller;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.search.document.FarmDocument;
import org.samtuap.inong.search.dto.BaseResponse;
import org.samtuap.inong.search.service.SearchService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 농장에서 검색 & 농장 데이터 인덱싱
     */
    @GetMapping("/farms")
    public BaseResponse<FarmDocument> searchFarms(@RequestParam String word1,
                                                  @RequestParam String word2) {
        return searchService.searchFarms(word1, word2);
    }

    @PostMapping("/farm/indexing")
    public String indexAllFarms() {
        searchService.indexAllFarms();
        return "농장 데이터 인덱싱 완료";
    }
}
