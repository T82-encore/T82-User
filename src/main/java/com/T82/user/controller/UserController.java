package com.T82.user.controller;

import com.T82.user.domain.dto.request.UserSignUpRequest;
import com.T82.user.domain.dto.request.UserWithDrawRequest;
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

//    유저 탈퇴
    @PostMapping("/user/withdraw")
    public void withDraw(@RequestBody UserWithDrawRequest userWithDrawRequest) {
        userService.withDrawUser(userWithDrawRequest);
    }


}
