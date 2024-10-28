package com.moin.remittance.controller.v1;

import com.moin.remittance.domain.dto.member.MemberDTO;
import com.moin.remittance.domain.dto.response.DataResponseDTO;
import com.moin.remittance.domain.dto.response.QuoteResponseDTO;
import com.moin.remittance.domain.dto.transfer.QuoteReqDTO;
import com.moin.remittance.domain.dto.transfer.QuoteRespDTO;
import com.moin.remittance.service.meber.MemberService;
import com.moin.remittance.service.transfer.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfer")
@Tag(name = "사용자", description = "사용자 관련 API")
public class RemittanceController {
    private final TransferService transferService;
    /**
     * EndPoint: GET /api/v2/transfer/quote
     * 기능: 두나무 오픈 API 스크래핑해서 환율이 적용된 송금 견적서를 리턴
     *
     * @RequestParam RemittanceQuoteRequestParamsDTO: 송금 견적서 요청 파라미터
     * @Param String amount: 원화
     * @Param String targetCurrency: 타겟 통화
     **/
    @Operation(summary = "두나무 오픈 API 스크래핑해서 환율이 적용된 송금 견적서를 리턴")
    @PostMapping(value = "/transfer/quote")
    public ResponseEntity<QuoteResponseDTO<QuoteRespDTO>> signup(@RequestBody @Valid QuoteReqDTO QuoteReqDTO) {
        QuoteRespDTO newMember = transferService.calculateQuote(QuoteReqDTO);
        return ResponseEntity.ok(QuoteResponseDTO.of(newMember, "OK"));
    }
}
