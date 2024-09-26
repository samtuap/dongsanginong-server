package org.samtuap.inong.search.controller;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.search.service.FarmSearchService;
import org.samtuap.inong.search.service.PackageProductSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/es")
@RequiredArgsConstructor
public class IntegratedSearchController {

    private final FarmSearchService farmSearchService;
    private final PackageProductSearchService packageProductSearchService;

    @GetMapping("/all")
    public ResponseEntity<List<Object>> searchAll(String keyword) {
        List<Object> searchResults = new ArrayList<>();

        // farm 검색
        searchResults.addAll(farmSearchService.searchFarms(keyword));
        // product 검색
        searchResults.addAll(packageProductSearchService.searchProducts(keyword));

        return new ResponseEntity<>(searchResults, HttpStatus.OK);
    }
}
