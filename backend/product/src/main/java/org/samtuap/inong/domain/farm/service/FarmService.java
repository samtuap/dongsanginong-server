package org.samtuap.inong.domain.farm.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.LiveFeign;
import org.samtuap.inong.common.client.MemberFeign;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.common.page.CustomPage;
import org.samtuap.inong.common.response.FavoriteGetResponse;
import org.samtuap.inong.domain.farm.dto.*;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.entity.FarmCategory;
import org.samtuap.inong.domain.farm.entity.FarmCategoryRelation;
import org.samtuap.inong.domain.farm.repository.FarmCategoryRelationRepository;
import org.samtuap.inong.domain.farm.repository.FarmCategoryRepository;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.samtuap.inong.domain.seller.dto.FarmCategoryResponse;
import org.samtuap.inong.domain.seller.dto.SellerFarmInfoUpdateRequest;
import org.samtuap.inong.search.document.FarmDocument;
import org.samtuap.inong.search.service.FarmSearchService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.samtuap.inong.common.exceptionType.FarmExceptionType.FARM_ALREADY_EXISTS;
import static org.samtuap.inong.common.exceptionType.FarmExceptionType.FARM_CATEGORY_NOT_FOUND;

@RequiredArgsConstructor
@Service
@Slf4j
public class FarmService {
    private final FarmRepository farmRepository;
    private final LiveFeign liveFeign;
    private final MemberFeign memberFeign;
    private final FarmCategoryRepository farmCategoryRepository;
    private final FarmCategoryRelationRepository farmCategoryRelationRepository;
    private final FarmSearchService farmSearchService;

    // 최신순, 스크랩순, 판매량 순
    @Cacheable(value = "FarmList", key = "#myId != null ? #myId : 'default'", cacheManager = "contentCacheManager")
    public CustomPage<FarmListGetResponse> getFarmList(Pageable pageable, Long myId) {
        Page<Farm> farmPage = farmRepository.findAll(pageable);
        List<FarmListGetResponse> responses = farmPage.stream()
                .map(farm -> {
                    FavoriteGetResponse favorite = (myId == null) ? null : memberFeign.getFavorite(myId, farm.getId());
                    return FarmListGetResponse.fromEntity(farm, favorite != null);
                })
                .collect(Collectors.toList());

        return new CustomPage<>(responses, pageable, farmPage.getTotalElements());
    }

    public FarmDetailGetResponse getFarmDetail(Long farmId) {
        return FarmDetailGetResponse.fromEntity(farmRepository.findByIdOrThrow(farmId));
    }

    public Page<FarmListGetResponse> farmSearch(String farmName, Pageable pageable, Long myId) {
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
        return farms.map(farm -> {
            FavoriteGetResponse favorite = memberFeign.getFavorite(myId, farm.getId());

            if(favorite == null) {
                return FarmListGetResponse.fromEntity(farm, false);
            } else {
                return FarmListGetResponse.fromEntity(farm, true);
            }
        });
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
                            response.sessionId(),
                            response.farmId(),
                            farmName,
                            response.title(),
                            response.liveImage(),
                            response.participantCount()
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


    /**
     * feign 요청용 (farmId로 입력받음)
     */
    public FarmSellerResponse getSellerIdByFarm(Long farmId) {
        Farm farm = farmRepository.findByIdOrThrow(farmId);
        return FarmSellerResponse.fromEntity(farm);
    }
  

    @Transactional
    public FarmCreateResponse createFarm(FarmCreateRequest request, Long sellerId) {

        if (farmRepository.existsBySellerId(sellerId)) {
            throw new BaseCustomException(FARM_ALREADY_EXISTS);
        }

        Farm farm = FarmCreateRequest.toEntity(request, sellerId);
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

        // elasticsearch✔️ : open search에 인덱싱
//        FarmDocument farmDocument = FarmDocument.convertToDocument(farm);
//        farmSearchService.indexFarmDocument(farmDocument);

        return FarmCreateResponse.fromEntity(farm);
    }

    public FarmInfoResponse getFarmInfo(Long sellerId) {
        Farm farm = farmRepository.findBySellerIdOrThrow(sellerId);
        List<FarmCategoryRelation> categoryRelations = farmCategoryRelationRepository.findAllByFarmId(farm.getId());
        List<Long> categories = new ArrayList<>();
        for (FarmCategoryRelation categoryRelation : categoryRelations) {
            FarmCategory category = categoryRelation.getCategory();
            categories.add(category.getId());
        }
        return FarmInfoResponse.fromEntity(farm, categories);
    }

    @Transactional
    public void updateFarmInfo(Long sellerId, SellerFarmInfoUpdateRequest infoUpdateRequest) {
        Farm farm = farmRepository.findBySellerIdOrThrow(sellerId);
        farm.updateInfo(infoUpdateRequest);
        farmCategoryRelationRepository.deleteAllByFarm(farm);
        for (Long categoryId : infoUpdateRequest.categoryId()) {
            FarmCategoryRelation newRelation = FarmCategoryRelation.builder()
                    .farm(farm)
                    .category(farmCategoryRepository.findByIdOrThrow(categoryId))
                    .build();
            farmCategoryRelationRepository.save(newRelation);
        }

        // elasticsearch✔️ : open search에 수정
//        FarmDocument farmDocument = FarmDocument.convertToDocument(farm);
//        farmSearchService.updateFarm(farmDocument);
    }

    public List<FarmCategoryResponse> getAllFarmCategories() {
        List<FarmCategory> categories = farmCategoryRepository.findAll();
        List<FarmCategoryResponse> categoryResponses = new ArrayList<>();
        for (FarmCategory category : categories) {
            categoryResponses.add(FarmCategoryResponse.fromEntity(category));
        }
        return categoryResponses;
    }

    public boolean checkFarmExistsBySellerId(Long sellerId) {
        return farmRepository.existsBySellerId(sellerId);
    }

    @Transactional
    public void increaseLike(Long farmId) {
        Farm farm = farmRepository.findByIdOrThrow(farmId);
        farm.updateFavoriteCount(farm.getFavoriteCount() + 1);
    }

    @Transactional
    public void decreaseLike(Long farmId) {
        Farm farm = farmRepository.findByIdOrThrow(farmId);
        farm.updateFavoriteCount(farm.getFavoriteCount() - 1);
    }

}
