package org.samtuap.inong.domain.seller.service;

import org.mindrot.jbcrypt.BCrypt;
import org.samtuap.inong.domain.seller.dto.SellerSignUpRequest;
import org.samtuap.inong.domain.seller.entity.Seller;
import org.samtuap.inong.domain.seller.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SellerService {

    private final SellerRepository sellerRepository;

    @Autowired
    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public Seller signUp(SellerSignUpRequest dto) {
        if (sellerRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        String encodedPassword = BCrypt.hashpw(dto.password(), BCrypt.gensalt());
        return sellerRepository.save(SellerSignUpRequest.toEntity(dto, encodedPassword));
    }
}
