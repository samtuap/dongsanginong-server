package org.samtuap.inong.domain.member.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.member.dto.*;
import org.samtuap.inong.domain.member.entity.MemberRole;
import org.samtuap.inong.domain.member.jwt.domain.JwtToken;
import org.samtuap.inong.domain.member.jwt.service.JwtService;
import org.samtuap.inong.domain.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/member")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtService jwtService;

    // 소셜 로그인
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(@RequestHeader("Authorization") final String authorizationHeader, @RequestBody final SignInRequest signInRequest) {
        String bearerToken = authorizationHeader.replace("Bearer ", "");
        SignInResponse signInResponse = memberService.signIn(bearerToken, signInRequest.socialType());

        return new ResponseEntity<>(signInResponse, HttpStatus.OK);
    }

    // 회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(@RequestHeader("Authorization") final String authorizationHeader, @RequestBody final SignUpRequest signUpRequest) {
        String socialAccessToken = authorizationHeader.replace("Bearer ", "");
        SignUpResponse signUpResponse = memberService.signUp(socialAccessToken, signUpRequest);

        return new ResponseEntity<>(signUpResponse, HttpStatus.CREATED);
    }

    @PostMapping("/sign-out")
    public ResponseEntity<SignOutRequest> signOut(@RequestBody final SignOutRequest signOutRequest){
        memberService.signOut(signOutRequest.memberId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withDraw(@RequestBody Long memberId){
        memberService.withdraw(memberId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/member-info")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@RequestBody Long memberId){
        MemberInfoResponse memberInfo =  memberService.getMemberInfo(memberId);
        return new ResponseEntity<>(memberInfo, HttpStatus.OK);
    }

    // 임시 코드 발급 > 추후 삭제 예정
    @GetMapping("/create-token")
    public JwtToken authTest(@RequestParam("id") Long memberId) {
        return jwtService.issueToken(memberId, MemberRole.MEMBER.toString());
    }

    /**
     * feignClient 요청용
     */
    @GetMapping("/{id}")
    public MemberDetailResponse findMember(@PathVariable("id") Long memberId) {
        return memberService.findMember(memberId);
    }

    @PatchMapping("/update-info")
    public ResponseEntity<MemberUpdateInfoRequest> updateMemberInfo(@RequestBody MemberUpdateInfoRequest updateInfo, @RequestParam("id") Long memberId){
        memberService.updateMemberInfo(updateInfo, memberId);
        return new ResponseEntity<>(updateInfo, HttpStatus.OK);
    }

    @GetMapping("/subscription")
    public ResponseEntity<MemberSubscriptionResponse> getSubscription(@RequestParam("id") Long memberId){
        MemberSubscriptionResponse memberSubscriptionResponse = memberService.getSubscription(memberId);
        return new ResponseEntity<>(memberSubscriptionResponse, HttpStatus.OK);

    }
}
