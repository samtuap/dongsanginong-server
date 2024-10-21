package org.samtuap.inong.domain.discount.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.discount.entity.Discount;
import org.samtuap.inong.domain.discount.repository.DiscountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;

    // 할인 생성
    @Transactional
    public Discount createDiscount(Discount discount) {
        // startAt이 오늘 날짜인 경우 즉시 활성화 처리
        if (discount.getStartAt().equals(LocalDate.now())) {
            discount.activateDiscount();
        }
        return discountRepository.save(discount);
    }

    // 할인 수정
    @Transactional
    public Discount updateDiscount(Long discountId, Discount discount) {
        Discount existingDiscount = discountRepository.findById(discountId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 할인 ID: " + discountId));

        // 기존 할인 정보 업데이트
//        existingDiscount.updateDiscount(discount.getDiscount(), discount.getStartAt(), discount.getEndAt());
//        existingDiscount.setDiscountActive(discount.isDiscountActive());
        return discountRepository.save(existingDiscount);
    }

    // 할인 삭제
    @Transactional
    public void deleteDiscount(Long discountId) {
        Discount existingDiscount = discountRepository.findById(discountId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 할인 ID: " + discountId));
        discountRepository.delete(existingDiscount);
    }
}
