package com.T82.user.service;

import com.T82.user.domain.dto.request.UserSignUpRequest;
import com.T82.user.domain.entity.User;

public interface UserService {
    void saveUser(UserSignUpRequest userSignUpRequest);
}
