package org.samtuap.inong.domain.seller.service;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.entity.FarmCategory;
import org.samtuap.inong.domain.farm.entity.FarmCategoryRelation;
import org.samtuap.inong.domain.farm.repository.FarmCategoryRelationRepository;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.samtuap.inong.domain.product.service.ImageService;
import org.samtuap.inong.domain.seller.dto.*;
import org.samtuap.inong.domain.seller.entity.Seller;
import org.samtuap.inong.domain.seller.entity.SellerRole;
import org.samtuap.inong.domain.seller.jwt.domain.JwtToken;
import org.samtuap.inong.domain.seller.jwt.service.JwtService;
import org.samtuap.inong.domain.seller.repository.SellerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.samtuap.inong.common.exceptionType.SellerExceptionType.*;

import java.util.List;


@RequiredArgsConstructor
@Service
@Transactional
public class SellerService {

    private final SellerRepository sellerRepository;
    private final FarmRepository farmRepository;
    private final FarmCategoryRelationRepository farmCategoryRelationRepository;
    private final JwtService jwtService;
    private final MailService mailService;
    private final ImageService imageService;

    @Transactional
    public SellerSignUpResponse verifyAndSignUp(EmailRequestDto requestDto) {
        if (!mailService.verifyAuthCode(requestDto.email(), requestDto.code())) {
            throw new BaseCustomException(CODE_INVALID);
        }

        SellerSignUpRequest dto = mailService.getUserData(requestDto.email(), SellerSignUpRequest.class);
        return signUp(dto);
    }

    @Transactional
    public SellerSignUpResponse signUp(SellerSignUpRequest dto) {
        validateSignUpRequest(dto);
        String encodedPassword = BCrypt.hashpw(dto.password(), BCrypt.gensalt());

        Seller seller = SellerSignUpRequest.toEntity(dto, encodedPassword);
        sellerRepository.save(seller);
        JwtToken jwtToken = jwtService.issueToken(seller.getId(), SellerRole.SELLER.toString());

        return SellerSignUpResponse.fromEntity(seller, jwtToken);
    }

    private void validateSignUpRequest(SellerSignUpRequest dto) {
        if (sellerRepository.findByEmail(dto.email()).isPresent()) {
            throw new BaseCustomException(EMAIL_ALREADY_REGISTERED);
        }

        if (dto.password().length() < 8) {
            throw new BaseCustomException(PASSWORD_TOO_SHORT);
        }
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

    @Transactional
    public SellerInfoResponse getSellerInfo(Long sellerId) {
        Seller seller = sellerRepository.findByIdOrThrow(sellerId);
        Farm farm = farmRepository.findBySellerIdOrThrow(seller.getId());
        List<FarmCategoryRelation> categoryRelation = farmCategoryRelationRepository.findAllByFarmId(farm.getId());
        List<String> farmCategory = categoryRelation.stream()
                .map(farmCategoryRelation -> farmCategoryRelation.getCategory().getTitle())
                .toList();

        return SellerInfoResponse.fromEntity(seller, farm, farmCategory);
    }

    @Transactional
    public void updateFarmInfo(Long sellerId, SellerFarmInfoUpdateRequest infoUpdateRequest) {
        Seller seller = sellerRepository.findByIdOrThrow(sellerId);
        Farm farm = farmRepository.findBySellerIdOrThrow(seller.getId());

        String bannerImageUrl = imageService.extractImageUrl(infoUpdateRequest.bannerImageUrl());
        String profileImageUrl = imageService.extractImageUrl(infoUpdateRequest.profileImageUrl());

        farm.updateInfo(infoUpdateRequest, bannerImageUrl, profileImageUrl);
        farmCategoryRelationRepository.deleteAllByFarm(farm);

        for (FarmCategory category : infoUpdateRequest.category()) {
            FarmCategoryRelation newRelation = FarmCategoryRelation.builder()
                    .farm(farm)
                    .category(category)
                    .build();
            farmCategoryRelationRepository.save(newRelation);
        }
    }

    @Transactional
    public void updatePassword(Long sellerId, SellerPasswordUpdateRequest passwordUpdate) {
        Seller seller = sellerRepository.findByIdOrThrow(sellerId);
        if (!BCrypt.checkpw(passwordUpdate.oldPassword(), seller.getPassword())) {
            throw new BaseCustomException(INVALID_PASSWORD);
        }
        String encodedNewPassword = BCrypt.hashpw(passwordUpdate.newPassword(), BCrypt.gensalt());
        seller.updatePassword(encodedNewPassword);
    }

}
