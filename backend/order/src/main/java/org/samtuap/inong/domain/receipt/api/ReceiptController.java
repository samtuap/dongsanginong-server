package org.samtuap.inong.domain.receipt.api;

import lombok.RequiredArgsConstructor;
import org.samtuap.inong.domain.receipt.dto.ReceiptInfoResponse;
import org.samtuap.inong.domain.receipt.service.ReceiptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/receipt")
@RestController
public class ReceiptController {
    private final ReceiptService receiptService;

    @GetMapping
    public ResponseEntity<ReceiptInfoResponse> getReceiptInfo(@RequestParam("id") Long receiptId){
        ReceiptInfoResponse receiptInfo = receiptService.getReceiptInfo(receiptId);
        return new ResponseEntity<>(receiptInfo, HttpStatus.OK);
    }

}
