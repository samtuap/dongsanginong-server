package org.samtuap.inong.domain.seller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.seller.dto.CommonResDto;
import org.samtuap.inong.domain.seller.dto.SellerSignUpRequest;
import org.samtuap.inong.domain.seller.entity.Seller;
import org.samtuap.inong.domain.seller.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SellerSignUpRequest dto) {
        Seller seller = sellerService.signUp(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "회원가입이 완료되었습니다.", seller.getBusinessName());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }
}
