package org.samtuap.inong.domain.member.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.member.dto.MemberDetailResponse;
import org.samtuap.inong.domain.member.service.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/member")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public String testApi() {
        return "hello world!!!";
    }

    /**
     * id로 회원 찾아오기 => product 모듈에서 feignclient로 찾아올 수 있도록 추가
     */
    @GetMapping("/{id}")
    public MemberDetailResponse findMember(@PathVariable("id") Long id) {

        return memberService.findMember(id);
    }
}
