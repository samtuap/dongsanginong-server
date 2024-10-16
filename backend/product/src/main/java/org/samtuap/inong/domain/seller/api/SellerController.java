package org.samtuap.inong.domain.seller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.product.dto.PackageProductUpdateRequest;
import org.samtuap.inong.domain.product.dto.SellerPackageListGetResponse;
import org.samtuap.inong.domain.product.service.PackageProductService;
import org.samtuap.inong.domain.seller.dto.*;
import org.samtuap.inong.domain.seller.entity.SellerRole;
import org.samtuap.inong.domain.seller.jwt.domain.JwtToken;
import org.samtuap.inong.domain.seller.jwt.service.JwtService;
import org.samtuap.inong.domain.seller.service.MailService;
import org.samtuap.inong.domain.seller.service.RedisTool;
import org.samtuap.inong.domain.seller.service.SellerService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
@Slf4j
public class SellerController {

    private final SellerService sellerService;
    private final MailService mailService;
    private final PackageProductService packageProductService;
    private final JwtService jwtService;
    private final RedisTool redisUtil;

    @PostMapping("/request-auth-code")
    public ResponseEntity<?> requestAuthCode(@RequestBody EmailRequestDto requestDto) {
        mailService.authEmail(requestDto.email());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody EmailRequestDto requestDto) {
        boolean isVerified = sellerService.verifyAuthCode(requestDto.email(), requestDto.code());
        if (isVerified) {
            redisUtil.setExpire(requestDto.email() + ":verified", true, 60 * 10L);
            redisUtil.delete(requestDto.email());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<SellerSignUpResponse> verifyAuthCode(@RequestBody SellerSignUpRequest dto) {
        Boolean isVerified = redisUtil.getValue(dto.email() + ":verified", Boolean.class);

        if(Boolean.TRUE.equals(isVerified)) {
            SellerSignUpResponse response = sellerService.signUp(dto);
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = sellerService.isEmailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
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
    public ResponseEntity<?> withDraw(@RequestHeader("sellerId") Long sellerId,
                                      @RequestBody String password) {
        sellerService.withDraw(sellerId, password);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<SellerInfoResponse> getSellerInfo(@RequestHeader("sellerId") Long sellerId){
        SellerInfoResponse sellerInfo =  sellerService.getSellerInfo(sellerId);
        return new ResponseEntity<>(sellerInfo, HttpStatus.OK);
    }

    @PatchMapping("/info/password/update")
    public ResponseEntity<?> updatePassword(@RequestHeader("sellerId") Long sellerId, @RequestBody SellerPasswordUpdateRequest passwordUpdate){
        sellerService.updatePassword(sellerId, passwordUpdate);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/packages")
    public ResponseEntity<Page<SellerPackageListGetResponse>> getSellerPackages(
            @RequestHeader("sellerId") Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SellerPackageListGetResponse> response = packageProductService.getSellerPackages(sellerId, page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/packages/{packageId}/delete")
    public void deletePackage(
            @RequestHeader("sellerId") Long sellerId,
            @PathVariable Long packageId) {
        packageProductService.deletePackage(sellerId, packageId);
    }

    @PutMapping("/packages/{packageId}/update")
    public void updatePackageProduct(
            @RequestHeader("sellerId") Long sellerId,
            @PathVariable Long packageId,
            @RequestBody PackageProductUpdateRequest request) {
        packageProductService.updatePackageProduct(sellerId, packageId, request);
    }

    // [임시 토큰 발급 API] TODO: 삭제 필요
    @GetMapping("/issue-seller-token")
    public JwtToken issueSellerToken(@RequestParam(value = "id") Long id){
        return jwtService.issueToken(id, SellerRole.SELLER.toString());
    }

}
