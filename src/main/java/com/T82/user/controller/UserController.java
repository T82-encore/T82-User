package com.T82.user.controller;

import com.T82.user.domain.dto.request.UserSignUpRequest;
import com.T82.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")

public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
//    @ResponseStatus(HttpStatus.OK)
    public void signup(@Validated @RequestBody UserSignUpRequest userSignUpRequest) {
        //유저 회원가입 로직
        userService.SignUpUser(userSignUpRequest);
    }

}
