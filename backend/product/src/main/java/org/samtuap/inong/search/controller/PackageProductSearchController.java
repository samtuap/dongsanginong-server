package org.samtuap.inong.search.controller;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.search.document.PackageProductDocument;
import org.samtuap.inong.search.service.PackageProductSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/es/product")
@RequiredArgsConstructor
public class PackageProductSearchController {

    private final PackageProductSearchService packageProductSearchService;

    // openSearch에 인덱싱 (=저장)
    @PostMapping("/index")
    public ResponseEntity<String> indexProduct(@RequestBody PackageProductDocument packageProductDocument) {
        packageProductSearchService.indexProductDocument(packageProductDocument);
        return new ResponseEntity<>("package document 인덱싱 완료", HttpStatus.OK);
    }

    // 검색
    @GetMapping("/search")
    public ResponseEntity<List<PackageProductDocument>> searchProducts(@RequestParam String keyword) {
        List<PackageProductDocument> products = packageProductSearchService.searchProducts(keyword);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        packageProductSearchService.deleteProduct(id);
        return new ResponseEntity<>("삭제 완료", HttpStatus.OK);
    }

    // 수정
    @PutMapping("/update")
    public ResponseEntity<String> updateProduct(@RequestBody PackageProductDocument packageProductDocument) {
        packageProductSearchService.updateProduct(packageProductDocument);
        return new ResponseEntity<>("수정 완료", HttpStatus.OK);
    }
}
