package org.samtuap.inong.domain.seller.api;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.samtuap.inong.domain.seller.service.EmailVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/email-verification")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;


    @PostMapping
    public ResponseEntity<?> sendCode(
            @Schema(description = "인증번호를 보낼 email 주소", example = "example@example.com")
            @RequestParam("email")
            String email) {
        emailVerificationService.sendCodeToEmail(email);
        return ResponseEntity.ok().body("발송완료");
    }


    @GetMapping
    public ResponseEntity<?> verifyCode(
            @Schema(description = "인증번호를 보낸 email 주소", example = "example@example.com")
            @RequestParam("email")
            String email,
            @Schema(description = "받은 인증번호", example = "123456")
            @RequestParam("code")
            String code) {
        return ResponseEntity.ok().body(emailVerificationService.verifyCode(email, code));
    }
}