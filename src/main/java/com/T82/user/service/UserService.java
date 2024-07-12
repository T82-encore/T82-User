package com.T82.user.service;

import com.T82.user.domain.dto.request.UserSignUpRequest;

public interface UserService {
    void SignUpUser(UserSignUpRequest userSignUpRequest);
}
