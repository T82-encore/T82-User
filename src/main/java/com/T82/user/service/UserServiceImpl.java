package com.T82.user.service;

import com.T82.user.domain.dto.request.*;
import com.T82.user.domain.dto.response.TokenResponse;
import com.T82.user.domain.dto.response.UserInfoResponse;
import com.T82.user.domain.entity.User;
import com.T82.user.domain.repository.UserRepository;
import com.T82.user.exception.*;
import com.T82.user.global.utils.JwtUtil;
import com.T82.user.global.utils.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void signUpUser(UserSignUpRequest userSignUpRequest) {
        User byEmail = userRepository.findByEmail(userSignUpRequest.email());
        if(byEmail != null) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
        }
        if (!Objects.equals(userSignUpRequest.password(), userSignUpRequest.passwordCheck()))
            throw new PasswordMissmatchException("비밀번호가 일치하지 않습니다.");
        User byPhone = userRepository.findByPhoneNumber(userSignUpRequest.phoneNumber());
        if(byPhone != null) {
            throw new DuplicateNumberException("이미 존재하는 휴대폰 번호입니다.");
        }
        String encodedPassword = passwordEncoder.encode(userSignUpRequest.password());
        userRepository.save(userSignUpRequest.toEntity(encodedPassword));
    }

    @Override
    public TokenResponse loginUser(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmail(userLoginRequest.email());
        if(user == null) {
            throw new NoEmailException("해당 이메일이 존재하지 않습니다.");
        }
        if(!passwordEncoder.matches(userLoginRequest.password(), user.getPassword())){
            throw new PasswordMissmatchException("비밀번호가 일치하지 않습니다.");
        }
        String token =jwtUtil.generateToken(user);
        System.out.println(token);
        return TokenResponse.from(token);
    }

    //    추후 토큰 형식에 맞춰 DTO 변경 필요
//    @Override
//    public UserInfoResponse getUserInfo(UserInfoRequest userInfoRequest) {
//        jwtUtil.parseToken(tok)
//        User byEmail = userRepository.findByEmail(userInfoRequest.email());
//        if(byEmail == null) {
//            throw new NoUserException("존재하지 않는 유저입니다.");
//        }
//        return UserInfoResponse.from(byEmail);
//    }

    //    추후 토큰 형식에 맞춰 DTO 변경 필요
    @Override
    public void withDrawUser(UserWithDrawRequest userWithDrawRequest) {
        User byEmail = userRepository.findByEmail(userWithDrawRequest.email());
        if(byEmail == null) {
            throw new NoUserException("존재하지 않는 유저입니다.");
        }
        byEmail.withDrawUser();
        userRepository.save(byEmail);
    }

    @Override
    public UserInfoResponse getUserInfo(String token) {
        User byEmail = userRepository.findByEmail(jwtUtil.parseToken(token).email());
        if(byEmail == null) {
            throw new NoUserException("존재하지 않는 유저입니다.");
        }
        return UserInfoResponse.from(byEmail);
    }

    //    추후 토큰 형식에 맞춰 DTO 변경 필요
    @Override
    public void updateUser(UserUpdateRequest userUpdateRequest) {
        User byName = userRepository.findByName(userUpdateRequest.name());
        if(byName == null) {
            throw new NoUserException("존재하지 않는 유저입니다.");
        }
        if (!Objects.equals(userUpdateRequest.password(), userUpdateRequest.passwordCheck()))
            throw new PasswordMissmatchException("비밀번호가 일치하지 않습니다.");
        byName.updateUser(userUpdateRequest.name(), userUpdateRequest.password(), userUpdateRequest.address(),userUpdateRequest.addressDetail());
        System.out.println(byName.getModifiedDate());
        userRepository.save(byName);
    }



}
