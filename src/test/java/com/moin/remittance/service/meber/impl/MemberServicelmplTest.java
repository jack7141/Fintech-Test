package com.moin.remittance.service.member;

import com.moin.remittance.domain.dto.member.MemberDTO;
import com.moin.remittance.domain.entity.member.MemberEntity;
import com.moin.remittance.exception.DuplicateUserIdException;
import com.moin.remittance.exception.InValidPatternTypeException;
import com.moin.remittance.repository.MemberRepository;
import com.moin.remittance.service.meber.impl.MemberServicelmpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private MemberServicelmpl memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("모든 회원 조회 - getMembers()")
    void getMembersTest() {
        List<MemberEntity> memberEntities = new ArrayList<>();
        memberEntities.add(
                MemberEntity.builder()
                        .userId("test@example.com")
                        .password("password")
                        .role("USER")
                        .name("Test User")
                        .idType("REG_NO")
                        .idValue("123456-7890123")
                        .build()
        );

        when(memberRepository.findAll()).thenReturn(memberEntities);

        List<MemberDTO> members = memberService.getMembers();

        assertNotNull(members);
        assertEquals(1, members.size());
        assertEquals("test@example.com", members.get(0).getUserId());

        verify(memberRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("새 회원 저장 - saveUser()")
    void saveUserTest() {
        MemberDTO newMemberDTO = MemberDTO.builder()
                .userId("unique@example.com")
                .password("password")
                .role("USER")
                .name("New User")
                .idType("REG_NO")
                .idValue("123456-7890123")
                .build();

        MemberEntity newMemberEntity = MemberEntity.builder()
                .userId("unique@example.com")
                .password(bCryptPasswordEncoder.encode("password"))
                .role("USER")
                .name("New User")
                .idType("REG_NO")
                .idValue(bCryptPasswordEncoder.encode("123456-7890123"))
                .build();

        when(memberRepository.existsByUserId(newMemberDTO.getUserId())).thenReturn(false);
        when(memberRepository.saveAndFlush(any(MemberEntity.class))).thenReturn(newMemberEntity);

        MemberDTO savedMember = memberService.saveUser(newMemberDTO);

        assertNotNull(savedMember);
        assertEquals(newMemberDTO.getUserId(), savedMember.getUserId());
        verify(memberRepository, times(1)).saveAndFlush(any(MemberEntity.class));
    }

    @Test
    @DisplayName("중복 회원 검사 - saveUser()")
    void saveUserDuplicateTest() {
        MemberDTO newMemberDTO = MemberDTO.builder()
                .userId("duplicate@example.com")
                .password("password")
                .role("USER")
                .name("Duplicate User")
                .idType("REG_NO")
                .idValue("123456-7890123")
                .build();

        when(memberRepository.existsByUserId(newMemberDTO.getUserId())).thenReturn(true);

        assertThrows(DuplicateUserIdException.class, () -> {
            memberService.saveUser(newMemberDTO);
        });

        verify(memberRepository, times(1)).existsByUserId(newMemberDTO.getUserId());
        verify(memberRepository, times(0)).saveAndFlush(any(MemberEntity.class));
    }

    @Test
    @DisplayName("잘못된 ID 패턴 검사 - saveUser()")
    void saveUserInvalidIdPatternTest() {
        MemberDTO newMemberDTO = MemberDTO.builder()
                .userId("invalid@example.com")
                .password("password")
                .role("USER")
                .name("Invalid User")
                .idType("REG_NO")
                .idValue("1234-567890") // invalid id value
                .build();

        assertThrows(InValidPatternTypeException.class, () -> {
            memberService.saveUser(newMemberDTO);
        });

        verify(memberRepository, times(0)).existsByUserId(newMemberDTO.getUserId());
        verify(memberRepository, times(0)).saveAndFlush(any(MemberEntity.class));
    }
}