package com.T82.user.controller;

import com.T82.user.domain.dto.request.*;
import com.T82.user.domain.dto.response.TokenResponse;
import com.T82.user.domain.dto.response.UserInfoResponse;
import com.T82.user.global.utils.TokenInfo;
import com.T82.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")

public class UserController {
    private final UserService userService;

    @GetMapping("/good")
    public ResponseEntity<String> firstPage() {
        return ResponseEntity.ok("초기화면");
    }

    @GetMapping("/bad")
    public ResponseEntity<String> padPage() {
        return ResponseEntity.ok("실패");
    }
//    유저 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Validated @RequestBody UserSignUpRequest userSignUpRequest) {
        userService.signUpUser(userSignUpRequest);
        return ResponseEntity.ok("회원가입 성공");
    }

//    유저 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Validated @RequestBody UserLoginRequest userLoginRequest) {
        return  ResponseEntity.status(HttpStatus.OK).body(userService.loginUser(userLoginRequest));
    }

//    토큰 재발급
    @GetMapping("/refresh")
    public TokenResponse refresh(@AuthenticationPrincipal TokenInfo tokenInfo) {
        return userService.refreshToken(tokenInfo);
    }

//    유저 정보 가져오기
    @GetMapping("/me")
    public UserInfoResponse getUserInfo (@AuthenticationPrincipal TokenInfo tokenInfo) {
        return userService.getUserInfo(tokenInfo);
    }

//    유저 정보 수정
    @PutMapping("/me")
    public ResponseEntity<String> updateUser(@AuthenticationPrincipal TokenInfo tokenInfo,
            @Validated @RequestBody UserUpdateRequest userUpdateRequest) {
        userService.updateUser(tokenInfo, userUpdateRequest);
        return ResponseEntity.ok("정보 수정 성공");
    }

//    유저 탈퇴
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal TokenInfo tokenInfo) {
        userService.deleteUser(tokenInfo);
        return ResponseEntity.ok("정보 삭제 성공");
    }








}
