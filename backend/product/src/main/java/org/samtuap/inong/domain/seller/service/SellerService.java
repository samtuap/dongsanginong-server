package org.samtuap.inong.domain.seller.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.farm.entity.Farm;
import org.samtuap.inong.domain.farm.entity.FarmCategoryRelation;
import org.samtuap.inong.domain.farm.repository.FarmCategoryRelationRepository;
import org.samtuap.inong.domain.farm.repository.FarmRepository;
import org.samtuap.inong.domain.product.repository.PackageProductRepository;
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
import java.util.Optional;


@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class SellerService {

    private final SellerRepository sellerRepository;
    private final FarmRepository farmRepository;
    private final FarmCategoryRelationRepository farmCategoryRelationRepository;
    private final JwtService jwtService;
    private final PackageProductRepository packageProductRepository;
    private final RedisTool redisUtil;

    @Transactional
    public boolean  verifyAuthCode(String email, String code) {
        String storedCode = redisUtil.getValue(email, String.class);
        log.info("Verifying code {} for email {}. Stored code: {}", code, email, storedCode);
        return code.equals(storedCode);
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
        Optional<Farm> farmOptional = farmRepository.findBySellerId(seller.getId());
        Long farm = farmOptional.map(Farm::getId).orElse(null);
        return SellerSignInResponse.fromEntity(seller, jwtToken, farm);
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

    @Transactional
    public void withDraw(Long sellerId, String password) {
        Seller seller = sellerRepository.findByIdOrThrow(sellerId);

        boolean isValidPassword = BCrypt.checkpw(password, seller.getPassword());
        if (!isValidPassword) {
            throw new BaseCustomException(INVALID_PASSWORD);
        }

        Optional<Farm> farmOptional = farmRepository.findBySellerId(seller.getId());

        if (farmOptional.isPresent()) {
            Farm farm = farmOptional.get();

            boolean hasActivePackages = packageProductRepository.existsByFarmIdAndDeletedAtIsNull(farm.getId());
            if (hasActivePackages) {
                throw new BaseCustomException(PACKAGES_EXIST);
            }

            // 농장 삭제 진행
            farmRepository.delete(farm);
            // elasticsearch✔️ : 삭제
//            farmSearchService.deleteFarm(String.valueOf(farm.getId()));
        }

        // 삭제되지 않은 패키지가 없으면 농장과 판매자 삭제 진행
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
    public void updatePassword(Long sellerId, SellerPasswordUpdateRequest passwordUpdate) {
        Seller seller = sellerRepository.findByIdOrThrow(sellerId);
        if (!BCrypt.checkpw(passwordUpdate.oldPassword(), seller.getPassword())) {
            throw new BaseCustomException(INVALID_PASSWORD);
        }
        String encodedNewPassword = BCrypt.hashpw(passwordUpdate.newPassword(), BCrypt.gensalt());
        seller.updatePassword(encodedNewPassword);
    }

    public boolean isEmailExists(String email) {
        return sellerRepository.existsByEmail(email);
    }
}
