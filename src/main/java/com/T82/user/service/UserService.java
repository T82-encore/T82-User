package com.T82.user.service;

import com.T82.user.domain.dto.request.UserSignUpRequest;
import com.T82.user.domain.dto.request.UserWithDrawRequest;

public interface UserService {
    void signUpUser(UserSignUpRequest userSignUpRequest);
    void withDrawUser(UserWithDrawRequest userWithDrawRequest);
}
