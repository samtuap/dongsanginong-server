package org.samtuap.inong.domain.farm.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.LiveFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.farm.dto.*;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.entity.FarmCategory;
import org.samtuap.inong.domain.farm.entity.FarmCategoryRelation;
import org.samtuap.inong.domain.farm.repository.FarmCategoryRelationRepository;
import org.samtuap.inong.domain.farm.repository.FarmCategoryRepository;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.samtuap.inong.common.S3.ImageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.samtuap.inong.common.exceptionType.FarmExceptionType.FARM_CATEGORY_NOT_FOUND;

@RequiredArgsConstructor
@Service
@Slf4j
public class FarmService {
    private final FarmRepository farmRepository;
    private final LiveFeign liveFeign;
    private final FarmCategoryRepository farmCategoryRepository;
    private final FarmCategoryRelationRepository farmCategoryRelationRepository;
    private final ImageService imageService;

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


    public List<FarmFavoriteResponse> getFarmFavoriteList(List<Long> farmFavoriteIds) {
        List<Farm> farmFavoriteList = farmRepository.findByIdIn(farmFavoriteIds);
        List<FarmFavoriteResponse> tmp = farmFavoriteList.stream()
                .map(FarmFavoriteResponse::fromEntity)
                .collect(Collectors.toList());
        return tmp;
    }


    /**
     * feign 요청용
     */
    @Transactional
    public List<FavoritesLiveListResponse> getFavoritesFarmLiveList(List<Long> favoriteFarmList) {
        List<Farm> list = farmRepository.findByIdIn(favoriteFarmList);
        List<Long> farmIdList = list.stream()
                .map(Farm::getId)
                .toList();
        return liveFeign.getFavoritesFarmLiveList(farmIdList).stream()
                .map(response -> {
                    String farmName = farmRepository.getFarmNameById(response.farmId());
                    return new FavoritesLiveListResponse(
                            response.id(),
                            response.farmId(),
                            farmName,
                            response.title(),
                            response.liveImage()
                    );
                })
                .toList();
    }

    /**
     * feign 요청용 (sellerId로 입력받음)
     */
    public FarmDetailGetResponse getFarmInfoWithSeller(Long sellerId) {
        Farm farm = farmRepository.findBySellerIdOrThrow(sellerId);
        return FarmDetailGetResponse.fromEntity(farm);
    }

    @Transactional
    public FarmCreateResponse createFarm(FarmCreateRequest request, Long sellerId) {
        String bannerImageUrl = imageService.extractImageUrl(request.bannerImageUrl());
        String profileImageUrl = imageService.extractImageUrl(request.profileImageUrl());

        Farm farm = FarmCreateRequest.toEntity(request, sellerId, bannerImageUrl, profileImageUrl);
        farm = farmRepository.save(farm);

        for (Long categoryId : request.categories()) {
            FarmCategory farmCategory = farmCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BaseCustomException(FARM_CATEGORY_NOT_FOUND));
            FarmCategoryRelation newRelation = FarmCategoryRelation.builder()
                    .farm(farm)
                    .category(farmCategory)
                    .build();
            farmCategoryRelationRepository.save(newRelation);
        }
        return FarmCreateResponse.fromEntity(farm);
    }
}
