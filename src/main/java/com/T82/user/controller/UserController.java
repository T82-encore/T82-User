package com.T82.user.controller;

import com.T82.user.domain.dto.request.*;
import com.T82.user.domain.dto.response.UserInfoResponse;
import com.T82.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")

public class UserController {
    private final UserService userService;

//    유저 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Validated @RequestBody UserSignUpRequest userSignUpRequest) {
        userService.signUpUser(userSignUpRequest);
        return ResponseEntity.ok("회원가입 성공");
    }

//    유저 로그인
    @PostMapping("/login")
    public void login(@Validated @RequestBody UserLoginRequest userLoginRequest) {
        userService.loginUser(userLoginRequest);
    }

//    유저 정보 가져오기
//    추후 토큰 형식에 맞춰 DTO, REST API Request 형태 변경 필요
    @GetMapping("/{email}")
    public UserInfoResponse getUserInfo(@Validated @PathVariable(name = "email") UserInfoRequest userInfoRequest) {
        return userService.getUserInfo(userInfoRequest);
    }


//    유저 탈퇴
//    추후 토큰 형식에 맞춰 DTO 변경 필요
    @DeleteMapping("/withdraw")
    public void withDraw(@Validated @RequestBody UserWithDrawRequest userWithDrawRequest) {
        userService.withDrawUser(userWithDrawRequest);
    }

//    유저 정보 수정
//    추후 토큰 형식에 맞춰 DTO 변경 필요
    @PostMapping("/update")
    public void updateUser(@Validated @RequestBody UserUpdateRequest userUpdateRequest) {
        userService.updateUser(userUpdateRequest);
    }





}
