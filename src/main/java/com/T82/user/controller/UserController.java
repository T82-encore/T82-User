package com.T82.user.controller;

import com.T82.user.domain.dto.request.UserSignUpRequest;
import com.T82.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")

public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.OK)
    public void signup(@Valid @RequestBody UserSignUpRequest userSignUpRequest) {
        //유저 회원가입 로직
        userService.saveUser(userSignUpRequest);
    }

}
