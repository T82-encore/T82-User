package com.T82.user.service;

import com.T82.user.domain.dto.request.*;
import com.T82.user.domain.dto.response.TokenResponse;
import com.T82.user.domain.dto.response.UserInfoResponse;
import com.T82.user.global.utils.TokenInfo;

public interface UserService {
    void signUpUser(UserSignUpRequest userSignUpRequest);
    TokenResponse loginUser(UserLoginRequest userLoginRequest);
    TokenResponse refreshToken(TokenInfo tokenInfo);
    UserInfoResponse getUserInfo(TokenInfo tokenInfo);
    void updateUser(TokenInfo tokenInfo, UserUpdateRequest userUpdateRequest);
    void deleteUser(TokenInfo tokenInfo);
    TokenResponse kakaoLogin(String accessToken);
    TokenResponse googleLogin(String token);
    void sendDeviceToken(DeviceTokenRequest req, TokenInfo tokenInfo);
}
