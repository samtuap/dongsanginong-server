package org.samtuap.inong.domain.discount.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.discount.dto.DiscountCreateRequest;
import org.samtuap.inong.domain.discount.dto.DiscountResponse;
import org.samtuap.inong.domain.discount.dto.DiscountUpdateRequest;
import org.samtuap.inong.domain.discount.entity.Discount;
import org.samtuap.inong.domain.discount.repository.DiscountRepository;
import org.samtuap.inong.domain.product.entity.PackageProduct;
import org.samtuap.inong.domain.product.repository.PackageProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.*;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final PackageProductRepository packageProductRepository;

    // 할인 생성
    @Transactional
    public DiscountResponse createDiscount(Long packageProductId, DiscountCreateRequest request) {
        PackageProduct packageProduct = packageProductRepository.findById(packageProductId)
                .orElseThrow(() -> new BaseCustomException(PRODUCT_NOT_FOUND));

        // 중복 체크 로직 추가
        boolean discountExists = discountRepository.existsByPackageProductAndStartAtAndEndAt(
                packageProduct, request.getStartAt(), request.getEndAt());
        if (discountExists) {
            throw new BaseCustomException(DISCOUNT_ALREADY_EXISTS);
        }

        Discount discount = request.toEntity(packageProduct);

        // startAt이 현재 시간보다 뒤에 있으면 활성화 설정
        if (discount.getStartAt().isBefore(LocalDate.now())) {
            discount.setDiscountActive(true);
        }

        Discount savedDiscount = discountRepository.save(discount);
        return toDto(savedDiscount);
    }

    // 할인 수정
    @Transactional
    public DiscountResponse updateDiscount(Long discountId, DiscountUpdateRequest request) {
        Discount existingDiscount = discountRepository.findById(discountId)
                .orElseThrow(() -> new BaseCustomException(FIELD_NOT_FOUND));

        request.updateEntity(existingDiscount);

        // startAt이 현재 시간보다 뒤에 있으면 활성화 설정
        if (existingDiscount.getStartAt().isBefore(LocalDate.now())) {
            existingDiscount.setDiscountActive(true);
        } else {
            existingDiscount.setDiscountActive(false);
        }

        Discount updatedDiscount = discountRepository.save(existingDiscount);
        return toDto(updatedDiscount);
    }

    // 할인 삭제
    @Transactional
    public void deleteDiscount(Long discountId) {
        Discount existingDiscount = discountRepository.findById(discountId)
                .orElseThrow(() -> new BaseCustomException(FIELD_NOT_FOUND));
        discountRepository.delete(existingDiscount);
    }

    // Discount를 DTO로 변환
    public DiscountResponse toDto(Discount discount) {
        return new DiscountResponse(
                discount.getId(),
                discount.getDiscount(),
                discount.getStartAt(),
                discount.getEndAt(),
                discount.isDiscountActive(),
                discount.getPackageProduct().getId()
        );
    }
}
