package com.T82.user.service;

import com.T82.user.domain.dto.request.UserSignUpRequest;
import com.T82.user.domain.entity.User;
import com.T82.user.domain.repository.UserRepository;
import com.T82.user.exception.DuplicateNumberException;
import com.T82.user.exception.PasswordMissmatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{
    private final UserRepository userRepository;

    @Override
    public void SignUpUser(UserSignUpRequest userSignUpRequest) {
        if (!Objects.equals(userSignUpRequest.password(), userSignUpRequest.passwordCheck()))
            throw new PasswordMissmatchException("비밀번호가 일치하지 않습니다.");
        User byPhone = userRepository.findByPhoneNumber(userSignUpRequest.phoneNumber());
        if(byPhone != null) {
            throw new DuplicateNumberException("이미 존재하는 휴대폰 번호입니다.");
        }
        userRepository.save(userSignUpRequest.toEntity(userSignUpRequest));
    }
}
