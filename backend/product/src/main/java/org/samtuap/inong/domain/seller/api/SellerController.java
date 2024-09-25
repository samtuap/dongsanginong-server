package org.samtuap.inong.domain.seller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.product.dto.PackageProductUpdateRequest;
import org.samtuap.inong.domain.product.dto.SellerPackageListGetResponse;
import org.samtuap.inong.domain.product.service.PackageProductService;
import org.samtuap.inong.domain.seller.dto.*;
import org.samtuap.inong.domain.seller.service.SellerService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
@RequiredArgsConstructor
public class SellerController {

    private final SellerService sellerService;
    private final PackageProductService packageProductService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signup(@Valid @RequestBody SellerSignUpRequest dto) {
        SellerSignUpResponse response = sellerService.signUp(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
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

    @GetMapping("/{sellerId}/packages")
    public ResponseEntity<Page<SellerPackageListGetResponse>> getSellerPackages(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SellerPackageListGetResponse> response = packageProductService.getSellerPackages(sellerId, page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{sellerId}/packages/{packageId}/delete")
    public void deletePackage(
            @PathVariable Long sellerId,
            @PathVariable Long packageId) {
        packageProductService.deletePackage(sellerId, packageId);
    }

    @PutMapping("/{sellerId}/packages/{packageId}/update")
    public void updatePackageProduct(
            @PathVariable Long sellerId,
            @PathVariable Long packageId,
            @RequestBody PackageProductUpdateRequest request) {
        packageProductService.updatePackageProduct(sellerId, packageId, request);
    }

    @PostMapping("/myfarm/info/update")
    public ResponseEntity<?> updateFarmInfo(@RequestHeader("sellerId") Long sellerId, @RequestBody SellerFarmInfoUpdateRequest infoUpdateRequest){
        sellerService.updateFarmInfo(sellerId, infoUpdateRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
