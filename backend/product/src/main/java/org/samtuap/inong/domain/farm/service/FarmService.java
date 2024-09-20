package org.samtuap.inong.domain.farm.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.farm.dto.FarmDetailGetResponse;
import org.samtuap.inong.domain.farm.dto.FarmListGetResponse;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FarmService {
    private final FarmRepository farmRepository;

    // 최신순, 스크랩순, 판매량 순
    public Page<FarmListGetResponse> getFarmList(Pageable pageable) {
        return farmRepository.findAll(pageable).map(FarmListGetResponse::fromEntity);
    }

    public FarmDetailGetResponse getFarmDetail(Long farmId) {
        return FarmDetailGetResponse.fromEntity(farmRepository.findByIdOrThrow(farmId));
    }

    public Page<FarmListGetResponse> farmSearch(String farmName, Pageable pageable) {
        Specification<Farm> specification = new Specification<Farm>() {
            @Override
            public Predicate toPredicate(Root<Farm> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                if (!farmName.isEmpty()) {
                    predicates.add(criteriaBuilder.like(root.get("farmName"), "%" + farmName + "%"));
                }

                // 만약 아무것도 없이 들어오면 그냥 findAll 되는 것임
                Predicate[] predicateArr = new Predicate[predicates.size()];
                for (int i = 0; i < predicateArr.length; i++) {
                    predicateArr[i] = predicates.get(i);
                }

                Predicate predicate = criteriaBuilder.and(predicateArr);
                return predicate;
            }
        };

        Page<Farm> farms = farmRepository.findAll(specification, pageable);
        return farms.map(FarmListGetResponse::fromEntity);
    }
}
