package org.samtuap.inong.domain.member.repository;

import org.samtuap.inong.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
