package com.moin.remittance.core.jwt.application;

import com.moin.remittance.core.jwt.provider.AuthUserDetailsProvider;
import com.moin.remittance.domain.entity.member.MemberEntity;
import com.moin.remittance.exception.NotFoundMemberException;
import com.moin.remittance.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUserDetailServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private AuthUserDetailService authUserDetailService;

    private MemberEntity testMember;

    @BeforeEach
    void setUp() {
        testMember = MemberEntity
                .builder()
                .userId("TestUser")
                .build();
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        when(memberRepository.findByUserId("testUser")).thenReturn(testMember);

        UserDetails userDetails = authUserDetailService.loadUserByUsername("testUser");

        assertNotNull(userDetails);
        assertTrue(userDetails instanceof AuthUserDetailsProvider);
        verify(memberRepository, times(1)).findByUserId("testUser");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        when(memberRepository.findByUserId("nonExistentUser")).thenReturn(null);

        assertThrows(NotFoundMemberException.class, () -> {
            authUserDetailService.loadUserByUsername("nonExistentUser");
        });

        verify(memberRepository, times(1)).findByUserId("nonExistentUser");
    }
}