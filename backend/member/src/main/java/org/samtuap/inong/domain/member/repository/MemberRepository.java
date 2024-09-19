package org.samtuap.inong.domain.member.repository;

import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.entity.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialIdAndSocialType(String socialId, SocialType socialType);
}
