package org.samtuap.inong.domain.seller.service;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.product.repository.PackageProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PackageProductDeleteService {

    private final PackageProductRepository packageProductRepository;

    // 농장에 삭제되지 않은 패키지가 있는지 확인
    public boolean hasActivePackages(Long farmId) {
        return packageProductRepository.existsByFarmIdAndDeletedAtIsNull(farmId);
    }
}

