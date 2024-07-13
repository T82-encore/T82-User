package com.T82.user.controller;

import com.T82.user.domain.dto.request.UserInfoRequest;
import com.T82.user.domain.dto.request.UserSignUpRequest;
import com.T82.user.domain.dto.request.UserWithDrawRequest;
import com.T82.user.domain.dto.response.UserInfoResponse;
import com.T82.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")

public class UserController {
    private final UserService userService;

//    유저 회원가입
    @PostMapping("/signup")
    public void signUp(@Validated @RequestBody UserSignUpRequest userSignUpRequest) {
        userService.signUpUser(userSignUpRequest);
    }

//    유저 정보 가져오기
//    추후 토큰 형식에 맞춰 DTO, REST API Request 형태 변경 필요
    @GetMapping("/user/{email}")
    public UserInfoResponse getUserInfo(@PathVariable(name = "email") UserInfoRequest userInfoRequest) {
        return userService.getUserInfo(userInfoRequest);
    }


//    유저 탈퇴
//    추후 토큰 형식에 맞춰 DTO 변경 필요
    @PostMapping("/user/withdraw")
    public void withDraw(@RequestBody UserWithDrawRequest userWithDrawRequest) {
        userService.withDrawUser(userWithDrawRequest);
    }



}
