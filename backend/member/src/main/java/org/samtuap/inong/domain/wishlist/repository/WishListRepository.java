package org.samtuap.inong.domain.wishlist.repository;

import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.wishlist.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishListRepository extends JpaRepository<WishList, Long> {

    Optional<WishList> findByMemberAndPackageProductId(Member member, Long packageProductId);

    List<WishList> findByMember(Member member);
}
