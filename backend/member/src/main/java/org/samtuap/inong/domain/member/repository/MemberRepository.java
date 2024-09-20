package org.samtuap.inong.domain.member.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.entity.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static org.samtuap.inong.common.exceptionType.MemberExceptionType.*;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialIdAndSocialType(String socialId, SocialType socialType);

    default Member findByIdOrThrow(Long memberId) {
        return findById(memberId).orElseThrow(() -> new BaseCustomException(MEMBER_NOT_FOUND));
    }
}
