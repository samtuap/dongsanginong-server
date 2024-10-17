package org.samtuap.inong.domain.wishlist.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.member.dto.PackageProductResponse;
import org.samtuap.inong.domain.wishlist.service.WishListService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/wishlist")
@RequiredArgsConstructor
@RestController
public class WishListController {

    private final WishListService wishListService;

    /**
     * 위시리스트 담기 / 취소
     */
    @PostMapping("/product/{packageProductId}")
    public ResponseEntity<Void> clickWishList(@RequestHeader("myId") Long memberId,
                                              @PathVariable("packageProductId") Long packageProductId) {
        wishListService.clickWishList(memberId, packageProductId);
        return ResponseEntity.ok(null);
    }

    /**
     * 내 위시리스트 조회
     */
    @GetMapping
    public ResponseEntity<List<PackageProductResponse>> getWishList(@RequestHeader("myId") Long memberId) {
        return new ResponseEntity<>(wishListService.getWishList(memberId), HttpStatus.OK);
    }
}
