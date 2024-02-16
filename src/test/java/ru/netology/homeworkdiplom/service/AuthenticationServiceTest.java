package ru.netology.homeworkdiplom.service;

import ru.netology.homeworkdiplom.dto.UserDto;
import ru.netology.homeworkdiplom.entity.UserEntity;
import ru.netology.homeworkdiplom.model.Token;
import ru.netology.homeworkdiplom.repository.UserRepository;
import ru.netology.homeworkdiplom.security.JWTToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootTest
public class AuthenticationServiceTest {

    private static final String AUTH_TOKEN = "auth-token";
    private static final String VALUE_TOKEN = "Bearer token";
    @Autowired
    private AuthenticationService authenticationService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private JWTToken jwtToken;
    @MockBean
    private PasswordEncoder passwordEncoder;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private UserEntity userEntity;
    private UserDto userDto;

    @BeforeEach
    public void init() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);

        userDto = UserDto.builder()
                .login("LoginTest")
                .password("PasswordTest")
                .build();
        userEntity = UserEntity.builder()
                .id(1L)
                .login("LoginTest")
                .password("PasswordTest")
                .build();
    }

    @Test
    public void test_authenticationLogin() {
        Mockito.when(userRepository.findUserByLogin(userDto.getLogin())).thenReturn(Optional.ofNullable(userEntity));
        Mockito.when(passwordEncoder.matches(userDto.getPassword(), userEntity.getPassword())).thenReturn(true);
        Mockito.when(jwtToken.generateToken(userEntity)).thenReturn(VALUE_TOKEN);

        Token generatedToken = authenticationService.authenticationLogin(userDto);

        Assertions.assertNotNull(generatedToken);
    }

    @Test
    @WithMockUser(username = "LoginTest",password = "PasswordTest")
    public void test_logout() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Mockito.when(userRepository.findUserByLogin(auth.getPrincipal().toString())).thenReturn(Optional.ofNullable(userEntity));

        String login = authenticationService.logout(AUTH_TOKEN, request, response);

        Assertions.assertEquals(login, userDto.getLogin());
    }
}