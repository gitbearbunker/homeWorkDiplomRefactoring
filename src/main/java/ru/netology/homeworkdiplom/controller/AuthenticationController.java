package ru.netology.homeworkdiplom.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.netology.homeworkdiplom.config.AuthenticationConfigConstants;
import ru.netology.homeworkdiplom.dto.UserDto;
import ru.netology.homeworkdiplom.model.Token;
import ru.netology.homeworkdiplom.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<Token> authenticationLogin(@RequestBody UserDto userDto) {
        log.info("Пользователь пытается войти в систему: {}", userDto);
        Token token = authenticationService.authenticationLogin(userDto);
        log.info("Пользователь: {} успешно вошел в систему. Auth-token: {}", userDto, token.getToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = AuthenticationConfigConstants.AUTH_TOKEN) String authToken,
                                       HttpServletRequest request, HttpServletResponse response) {
        String userLogout = authenticationService.logout(authToken, request, response);
        if (userLogout == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.info("Пользователь: {} успешно вышел из системы. Auth-token: {}", userLogout, authToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}