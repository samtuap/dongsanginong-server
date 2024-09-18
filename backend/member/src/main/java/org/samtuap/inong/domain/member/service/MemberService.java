package org.samtuap.inong.domain.member.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.member.dto.MemberDetailResponse;
import org.samtuap.inong.domain.member.entity.Member;
import org.samtuap.inong.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * id로 회원 찾아오기
     */
    public MemberDetailResponse findMember(Long id) {

        Member member = memberRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("해당 id의 회원이 존재하지 않습니다.")
        );
        return MemberDetailResponse.from(member);
    }
}
