package com.moin.remittance.controller.v1;
import com.moin.remittance.domain.dto.member.MemberDTO;
import com.moin.remittance.domain.dto.member.MemberLoginReqDTO;
import com.moin.remittance.domain.dto.response.DataResponseDTO;
import com.moin.remittance.domain.dto.response.HttpResponseBody;
import com.moin.remittance.service.meber.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "사용자", description = "사용자 관련 API")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("")
    public DataResponseDTO<List<MemberDTO>> getUsers() {
        List<MemberDTO> members = memberService.getMembers();
        System.out.println(members);
        return DataResponseDTO.of(members);
    }

    /**
     * 회원 가입
     *
     * @RequestBody properties
     * userId: 유저 아이디(이메일 형식)
     * password : 비밀번호
     * name : 이름
     */
    @Operation(summary = "회원 가입")
    @PostMapping(value = "/signup")
    public ResponseEntity<DataResponseDTO<MemberDTO>> signup(@RequestBody  @Valid MemberDTO MemberDTO) {
        MemberDTO newMember = memberService.saveUser(MemberDTO);
        return ResponseEntity.ok(DataResponseDTO.of(newMember, "회원가입이 성공적으로 완료되었습니다."));
    }

    @Operation(summary = "로그인 엔드포인트(swagger용 엔드포인트: 서블릿 필터에서 응답함)")
    @PostMapping(value = "/login",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<HttpResponseBody<?>> login(@RequestBody @Valid MemberLoginReqDTO memberDTO) {
        return ResponseEntity.status(200).body(
                HttpResponseBody.builder()
                        .statusCode(200)
                        .message("Login successful")
                        .codeName("SUCCESS")
                        .token("토큰")
                        .build()
        );
    }
}

