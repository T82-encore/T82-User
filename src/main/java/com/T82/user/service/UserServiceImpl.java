package com.T82.user.service;

import com.T82.user.domain.dto.request.UserSignUpRequest;
import com.T82.user.domain.entity.User;
import com.T82.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{
    private final UserRepository userRepository;
    @Override
    public void saveUser(UserSignUpRequest userSignUpRequest) {
        userRepository.save(userSignUpRequest.toEntity(userSignUpRequest));
    }
}
