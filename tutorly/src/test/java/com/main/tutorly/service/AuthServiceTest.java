package com.main.tutorly.service;

import com.main.tutorly.dto.AuthResponse;
import com.main.tutorly.dto.LoginRequest;
import com.main.tutorly.dto.RegisterRequest;
import com.main.tutorly.entity.User;
import com.main.tutorly.repository.UserRepository;
import com.main.tutorly.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setRole(User.Role.STUDENT);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(User.Role.STUDENT);
    }

    @Test
    void register_SuccessfulRegistration_ReturnsAuthResponse() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(mock(UserDetails.class));
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getRole(), response.getRole());

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    void register_EmailAlreadyExists_ThrowsRuntimeException() {
        // Arrange
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.register(registerRequest));
        assertEquals("Email already exists", exception.getMessage());

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verifyNoMoreInteractions(passwordEncoder, userRepository, jwtUtil);
    }

    @Test
    void login_ValidCredentials_ReturnsAuthResponse() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(testUser));
        when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(mock(UserDetails.class));
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("jwt-token");
        // Mock successful authentication (authenticate is void, so no when() needed)

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getRole(), response.getRole());

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmail(loginRequest.getEmail());
        verify(userDetailsService).loadUserByUsername(loginRequest.getEmail());
        verify(jwtUtil).generateToken(any(UserDetails.class));
    }

    @Test
    void login_UserNotFound_ThrowsRuntimeException() {
        // Arrange
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());
        when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(mock(UserDetails.class));
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("jwt-token");
        // Mock successful authentication (user exists for auth but not found after)

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.login(loginRequest));
        assertEquals("User not found", exception.getMessage());

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmail(loginRequest.getEmail());
    }

    @Test
    void login_InvalidPassword_ThrowsRuntimeException() {
        // Arrange
        // Mock authentication failure
        doThrow(new RuntimeException("Authentication failed")).when(authenticationManager).authenticate(any());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> authService.login(loginRequest));
        assertEquals("Authentication failed", exception.getMessage());

        verify(authenticationManager).authenticate(any());
    }
}
