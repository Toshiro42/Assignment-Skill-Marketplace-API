package com.Skill.Marketplace.SM.Services;

import com.Skill.Marketplace.SM.Security.JWTUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailService userDetailsService;

    @Mock
    private JWTUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldLoginAndReturnToken() {

        // fake user returned by DB
        UserDetails userDetails = User.withUsername("manan")
                .password("pass")
                .authorities("USER")
                .build();

        when(userDetailsService.loadUserByUsername("manan"))
                .thenReturn(userDetails);

        when(jwtUtil.generateToken(userDetails))
                .thenReturn("mocked-jwt-token");

        String token = authService.login("manan", "12345678");

        assertEquals("mocked-jwt-token", token);
        verify(authenticationManager).authenticate(any());
    }
}
