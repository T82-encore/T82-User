package com.T82.user.service;

import com.T82.user.domain.dto.request.*;
import com.T82.user.domain.dto.response.TokenResponse;
import com.T82.user.domain.dto.response.UserInfoResponse;

public interface UserService {
    void signUpUser(UserSignUpRequest userSignUpRequest);
    TokenResponse loginUser(UserLoginRequest userLoginRequest);
    UserInfoResponse getUserInfo(String token);
    void updateUser(String token, UserUpdateRequest userUpdateRequest);
    void deleteUser(String token);
}
