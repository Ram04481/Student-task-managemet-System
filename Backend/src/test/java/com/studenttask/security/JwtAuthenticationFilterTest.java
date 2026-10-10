package com.studenttask.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();

        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_NoAuthHeader_ContinuesChain() throws Exception {
        request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/tasks");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, never()).extractEmail(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void doFilterInternal_InvalidAuthHeader_ContinuesChain() throws Exception {
        request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/tasks");
        request.addHeader("Authorization", "InvalidToken");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, never()).extractEmail(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void doFilterInternal_ValidToken_SetsAuthentication() throws Exception {
        String token = "valid.jwt.token";
        String email = "test@example.com";

        request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/tasks");
        request.addHeader("Authorization", "Bearer " + token);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(email)
                .password("password")
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                .build();

        when(jwtService.extractEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, email)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals(email, auth.getName());
        assertEquals(userDetails, auth.getPrincipal());
    }

    @Test
    void doFilterInternal_InvalidToken_DoesNotSetAuthentication() throws Exception {
        String token = "invalid.jwt.token";
        String email = "test@example.com";

        request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/tasks");
        request.addHeader("Authorization", "Bearer " + token);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(email)
                .password("password")
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                .build();

        when(jwtService.extractEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, email)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    void doFilterInternal_TokenExpired_DoesNotSetAuthentication() throws Exception {
        String token = "expired.jwt.token";
        String email = "test@example.com";

        request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/tasks");
        request.addHeader("Authorization", "Bearer " + token);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(email)
                .password("password")
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                .build();

        when(jwtService.extractEmail(token)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, email)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
    }

    @Test
    void doFilterInternal_ExceptionDuringProcessing_ContinuesChain() throws Exception {
        String token = "valid.jwt.token";
        String email = "test@example.com";

        request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/tasks");
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtService.extractEmail(token)).thenThrow(new RuntimeException("Token parsing failed"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void doFilterInternal_PublicEndpoint_ContinuesWithoutAuth() throws Exception {
        request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/api/health");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, never()).extractEmail(anyString());
    }

    @Test
    void doFilterInternal_AuthEndpoint_ContinuesWithoutAuth() throws Exception {
        request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setRequestURI("/api/auth/login");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, never()).extractEmail(anyString());
    }
}