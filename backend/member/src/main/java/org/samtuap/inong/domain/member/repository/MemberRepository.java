package org.samtuap.inong.domain.member.repository;

import org.samtuap.inong.common.exception.BaseCustomException;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.entity.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import static org.samtuap.inong.common.exceptionType.MemberExceptionType.*;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialIdAndSocialType(String socialId, SocialType socialType);

    default Member findByIdOrThrow(Long memberId) {
        return findById(memberId).orElseThrow(() -> new BaseCustomException(MEMBER_NOT_FOUND));
    }


    @Query("SELECT m FROM Member m WHERE m.id IN :ids")
    List<Member> findByAllByIds(@Param("ids") List<Long> ids);
}
