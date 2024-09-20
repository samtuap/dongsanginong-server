package org.samtuap.inong.domain.seller.service;

import org.mindrot.jbcrypt.BCrypt;
import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.seller.dto.SellerSignInRequest;
import org.samtuap.inong.domain.seller.dto.SellerSignInResponse;
import org.samtuap.inong.domain.seller.dto.SellerSignUpRequest;
import org.samtuap.inong.domain.seller.entity.Seller;
import org.samtuap.inong.domain.seller.jwt.domain.JwtToken;
import org.samtuap.inong.domain.seller.jwt.service.JwtService;
import org.samtuap.inong.domain.seller.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.samtuap.inong.common.exceptionType.ProductExceptionType.EMAIL_NOT_FOUND;
import static org.samtuap.inong.common.exceptionType.ProductExceptionType.INVALID_PASSWORD;

@Service
@Transactional
public class SellerService {

    private final SellerRepository sellerRepository;
    private final JwtService jwtService;

    @Autowired
    public SellerService(SellerRepository sellerRepository, JwtService jwtService) {
        this.sellerRepository = sellerRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public Seller signUp(SellerSignUpRequest dto) {
        if (sellerRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        String encodedPassword = BCrypt.hashpw(dto.password(), BCrypt.gensalt());
        return sellerRepository.save(SellerSignUpRequest.toEntity(dto, encodedPassword));
    }

    public SellerSignInResponse signIn(SellerSignInRequest dto) {
        Seller seller = validateSellerCredentials(dto);
        JwtToken jwtToken = jwtService.issueToken(seller.getId());
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
}
