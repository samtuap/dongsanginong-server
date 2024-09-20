package org.samtuap.inong.domain.seller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.seller.dto.SellerSignInRequest;
import org.samtuap.inong.domain.seller.dto.SellerSignInResponse;
import org.samtuap.inong.domain.seller.dto.SellerSignOutRequest;
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

    @PostMapping("/sign-up")
    public ResponseEntity<?> signup(@Valid @RequestBody SellerSignUpRequest dto) {
        Seller seller = sellerService.signUp(dto);
        return new ResponseEntity<>("회원가입이 완료되었습니다: " + seller.getBusinessName(), HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody SellerSignInRequest dto){
        SellerSignInResponse sellerSignInResponse = sellerService.signIn(dto);
        return new ResponseEntity<>(sellerSignInResponse, HttpStatus.OK);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<SellerSignOutRequest> signOut(@RequestBody final SellerSignOutRequest sellerSignOutRequest){
        sellerService.signOut(sellerSignOutRequest.sellerId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withDraw(@RequestBody Long sellerId) {
        sellerService.withDraw(sellerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
