package com.moin.remittance.controller.v1;

import com.moin.remittance.domain.dto.member.MemberDTO;
import com.moin.remittance.domain.dto.response.DataResponseDTO;
import com.moin.remittance.domain.dto.response.QuoteResponseDTO;
import com.moin.remittance.domain.dto.response.TradeHistResponseDTO;
import com.moin.remittance.domain.dto.trade.TradeHistoryDTO;
import com.moin.remittance.domain.dto.trade.TradeRespDTO;
import com.moin.remittance.domain.dto.transfer.QuoteReqDTO;
import com.moin.remittance.domain.dto.transfer.QuoteRespDTO;
import com.moin.remittance.service.meber.MemberService;
import com.moin.remittance.service.trade.TradeService;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfer")
@Tag(name = "사용자", description = "사용자 관련 API")
public class RemittanceController {
    private final TransferService transferService;
    private final TradeService tradeService;

    /**
     * EndPoint: GET /api/v2/transfer/quote
     * 기능: 두나무 오픈 API 스크래핑해서 환율이 적용된 송금 견적서를 리턴
     *
     * @RequestParam RemittanceQuoteRequestParamsDTO: 송금 견적서 요청 파라미터
     * @Param String amount: 원화
     * @Param String targetCurrency: 타겟 통화
     **/
    @Operation(summary = "두나무 오픈 API 스크래핑해서 환율이 적용된 송금 견적서를 리턴")
    @PostMapping(value = "/quote")
    public ResponseEntity<QuoteResponseDTO<QuoteRespDTO>> signup(@RequestBody @Valid QuoteReqDTO QuoteReqDTO) {
        QuoteRespDTO newMember = transferService.calculateQuote(QuoteReqDTO);
        return ResponseEntity.ok(QuoteResponseDTO.of(newMember, "OK"));
    }

    @Operation(summary = "회원의 거래 이력을 리턴")
    @GetMapping(value = "/list")
    public ResponseEntity<TradeHistResponseDTO> getUserTradeList() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TradeRespDTO TradeRespDTO = tradeService.getUserTrade(userId);
        TradeHistResponseDTO responseDTO = TradeHistResponseDTO.of(
                "success",
                200,
                "OK",
                TradeRespDTO.getUserId(),
                TradeRespDTO.getName(), // 사용자 이름 예제
                TradeRespDTO.getTodayTransferCount(), // 오늘 송금 횟수 (예제)
                TradeRespDTO.getTodayTransferUsdAmount(), // 오늘 송금 금액 (예제)
                TradeRespDTO.getHistory()
        );

        return ResponseEntity.ok(responseDTO);
    }
}
