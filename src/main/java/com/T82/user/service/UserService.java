package com.T82.user.service;

import com.T82.user.domain.dto.request.UserInfoRequest;
import com.T82.user.domain.dto.request.UserSignUpRequest;
import com.T82.user.domain.dto.request.UserWithDrawRequest;
import com.T82.user.domain.dto.response.UserInfoResponse;

public interface UserService {
    void signUpUser(UserSignUpRequest userSignUpRequest);
    //    추후 토큰 형식에 맞춰 DTO 변경 필요
    void withDrawUser(UserWithDrawRequest userWithDrawRequest);
    //    추후 토큰 형식에 맞춰 DTO 변경 필요
    UserInfoResponse getUserInfo(UserInfoRequest userInfoRequest);
}
