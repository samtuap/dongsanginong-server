package org.samtuap.inong.domain.discount.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.discount.entity.Discount;
import org.samtuap.inong.domain.discount.service.DiscountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    // 할인 생성
    @PostMapping
    public ResponseEntity<Discount> createDiscount(@RequestBody Discount discount) {
        Discount createdDiscount = discountService.createDiscount(discount);
        return ResponseEntity.ok(createdDiscount);
    }

    // 할인 수정
    @PutMapping("/{discountId}")
    public ResponseEntity<Discount> updateDiscount(
            @PathVariable Long discountId,
            @RequestBody Discount discount) {
        Discount updatedDiscount = discountService.updateDiscount(discountId, discount);
        return ResponseEntity.ok(updatedDiscount);
    }

    // 할인 삭제
    @DeleteMapping("/{discountId}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long discountId) {
        discountService.deleteDiscount(discountId);
        return ResponseEntity.noContent().build();
    }
}
