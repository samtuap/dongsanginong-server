package org.samtuap.inong.domain.wishlist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.common.client.ProductFeign;
import org.samtuap.inong.domain.member.dto.PackageProductResponse;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.repository.MemberRepository;
import org.samtuap.inong.domain.wishlist.entity.WishList;
import org.samtuap.inong.domain.wishlist.repository.WishListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class WishListService {

    private final WishListRepository wishListRepository;
    private final MemberRepository memberRepository;
    private final ProductFeign productFeign;

    @Transactional
    public void clickWishList(Long memberId, Long packageProductId) {
        Member member = memberRepository.findByIdOrThrow(memberId);

        Optional<WishList> wishListOpt = wishListRepository.findByMemberAndPackageProductId(member, packageProductId);
        if (wishListOpt.isPresent()) {
            // 이미 위시리스트 했으면 => 취소
            wishListRepository.delete(wishListOpt.get());
            // product 모듈에 feign 요청
            productFeign.decreaseWish(packageProductId);
        } else {
            // 위시리스트에 없는 항목이면 => 추가
            WishList wishList = WishList.builder()
                    .member(member)
                    .packageProductId(packageProductId)
                    .build();
            wishListRepository.save(wishList);
            productFeign.increaseWish(packageProductId);
        }
    }

    /**
     * 위시리스트에 추가된 상품 목록 출력
     */
    public List<PackageProductResponse> getWishList(Long memberId) {
        Member member = memberRepository.findByIdOrThrow(memberId);
        List<WishList> wishList = wishListRepository.findByMember(member);
        List<PackageProductResponse> dtoList = new ArrayList<>();

        for (WishList wish : wishList) {
            PackageProductResponse product = productFeign.getPackageProduct(wish.getPackageProductId());
            dtoList.add(PackageProductResponse.from(product));
        }
        return dtoList;
    }
}
