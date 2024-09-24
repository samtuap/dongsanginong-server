package org.samtuap.inong.domain.seller.service;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.entity.FarmCategory;
import org.samtuap.inong.domain.farm.entity.FarmCategoryRelation;
import org.samtuap.inong.domain.farm.repository.FarmCategoryRelationRepository;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.samtuap.inong.domain.seller.dto.*;
import org.samtuap.inong.domain.seller.entity.Seller;
import org.samtuap.inong.domain.seller.entity.SellerRole;
import org.samtuap.inong.domain.seller.jwt.domain.JwtToken;
import org.samtuap.inong.domain.seller.jwt.service.JwtService;
import org.samtuap.inong.domain.seller.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.*;

@RequiredArgsConstructor
@Service
@Transactional
public class SellerService {

    private final SellerRepository sellerRepository;
    private final FarmRepository farmRepository;
    private final FarmCategoryRelationRepository farmCategoryRelationRepository;
    private final JwtService jwtService;

    @Transactional
    public SellerSignUpResponse signUp(SellerSignUpRequest dto) {
        if (sellerRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        String encodedPassword = BCrypt.hashpw(dto.password(), BCrypt.gensalt());
        Seller seller = SellerSignUpRequest.toEntity(dto, encodedPassword);
        sellerRepository.save(seller);
        JwtToken jwtToken = jwtService.issueToken(seller.getId(), SellerRole.SELLER.toString());
        return SellerSignUpResponse.fromEntity(seller, jwtToken);
    }

    @Transactional
    public SellerSignInResponse signIn(SellerSignInRequest dto) {
        Seller seller = validateSellerCredentials(dto);
        JwtToken jwtToken = jwtService.issueToken(seller.getId(), SellerRole.SELLER.toString());
        return SellerSignInResponse.fromEntity(seller, jwtToken);
    }

    @Transactional
    public void signOut(final Long sellerId) {
        jwtService.deleteRefreshToken(sellerId);
    }

    private Seller validateSellerCredentials(SellerSignInRequest dto) {
        Seller seller = sellerRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BaseCustomException(EMAIL_NOT_FOUND));

        // BCrypt 비밀번호 검증
        if (!BCrypt.checkpw(dto.password(), seller.getPassword())) {
            throw new BaseCustomException(INVALID_PASSWORD);
        }
        return seller;
    }

    public void withDraw(Long sellerId) {
        Seller seller = sellerRepository.findByIdOrThrow(sellerId);
        sellerRepository.deleteById(seller.getId());
        jwtService.deleteRefreshToken(seller.getId());
    }

    public SellerInfoResponse getSellerInfo(Long sellerId) {
        Seller seller = sellerRepository.findByIdOrThrow(sellerId);
        Farm farm = farmRepository.findBySellerIdOrThrow(seller.getId());
        List<FarmCategoryRelation> categoryRelation = farmCategoryRelationRepository.findAllByFarmId(farm.getId());
        List<String> farmCategory = categoryRelation.stream()
                .map(farmCategoryRelation -> farmCategoryRelation.getCategory().getTitle())
                .toList();

        return SellerInfoResponse.fromEntity(seller, farm, farmCategory);
    }


    public void updateFarmInfo(Long sellerId, SellerFarmInfoUpdateRequest infoUpdateRequest) {
        Seller seller = sellerRepository.findByIdOrThrow(sellerId);
        Farm farm = farmRepository.findBySellerIdOrThrow(seller.getId());
        farm.updateInfo(infoUpdateRequest);
        farmCategoryRelationRepository.deleteAllByFarm(farm);
        for (FarmCategory category : infoUpdateRequest.category()) {
            FarmCategoryRelation newRelation = FarmCategoryRelation.builder()
                    .farm(farm)
                    .category(category)
                    .build();
            farmCategoryRelationRepository.save(newRelation);
        }
    }
}
