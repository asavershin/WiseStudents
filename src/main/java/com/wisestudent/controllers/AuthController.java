package com.wisestudent.controllers;

import com.wisestudent.dto.user.RequestLoginUser;
import com.wisestudent.dto.user.RequestUser;
import com.wisestudent.dto.user.ResponseUser;
import com.wisestudent.mappers.UserMapper;
import com.wisestudent.security.TokenService;
import com.wisestudent.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

;

//@CrossOrigin
@RestController
@RequestMapping("/wise-students/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;
    private final UserMapper userMapper;
    private final TokenService tokenService;

    @GetMapping("/logout")
    public ResponseEntity<Object> logoutUser() {

        return ResponseEntity.ok().header(
                HttpHeaders.SET_COOKIE,
                ResponseCookie.from("authToken", "")
                        .httpOnly(true)
                        .path("/")
                        .maxAge(0)
                        .build()
                        .toString()
        ).build();

    }

    @PostMapping("/login")
    public ResponseEntity<ResponseUser> loginUser(@Valid @RequestBody RequestLoginUser requestUser) {

        ResponseUser user = userMapper.userToResponseUser(authService.login(requestUser.getLogin(), requestUser.getPassword()));

        return ResponseEntity.ok().header(
                HttpHeaders.SET_COOKIE,
                ResponseCookie.from("authToken", tokenService.generateToken(user.getLogin()))
                        .httpOnly(true)
                        .path("/")
                        .maxAge(60 * 60 * 24 * 7)
                        .build()
                        .toString()
        ).body(user);

    }

    @PostMapping("/registration")
    public ResponseEntity<ResponseUser> registerUser(@Valid @RequestBody RequestUser requestUser) {

        ResponseUser user = userMapper.userToResponseUser(
                authService.register(
                        userMapper.requestUserToUser(requestUser)
                )
        );

        return ResponseEntity.ok().header(
                HttpHeaders.SET_COOKIE,
                ResponseCookie.from("authToken", tokenService.generateToken(user.getLogin()))
                        .httpOnly(true)
                        .path("/")
                        .maxAge(60 * 60 * 24 * 7)
                        .build()
                        .toString()
        ).body(user);

    }

}
