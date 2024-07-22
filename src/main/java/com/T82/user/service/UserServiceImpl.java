package com.T82.user.service;

import com.T82.user.domain.dto.request.*;
import com.T82.user.domain.dto.response.TokenResponse;
import com.T82.user.domain.dto.response.UserInfoResponse;
import com.T82.user.domain.entity.User;
import com.T82.user.domain.repository.UserRepository;
import com.T82.user.exception.*;
import com.T82.user.global.utils.JwtUtil;
import com.T82.user.global.utils.TokenInfo;
import com.T82.user.kafka.dto.request.KafkaUserDeleteRequest;
import com.T82.user.kafka.dto.request.KafkaUserSignUpRequest;
import com.T82.user.kafka.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KafkaProducer kafkaProducer;

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
        User user = userRepository.save(userSignUpRequest.toEntity(encodedPassword));
        KafkaUserSignUpRequest kafkaUserSignUpRequest =
                new KafkaUserSignUpRequest(user.getUserId(), user.getEmail(), user.getIsDeleted());
        System.out.println(kafkaUserSignUpRequest.userId());
        kafkaProducer.sendSignUp(kafkaUserSignUpRequest, "signup-topic");
    }

    @Override
    public TokenResponse loginUser(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmail(userLoginRequest.email());
        if(user == null) {
            throw new NoEmailException("존재하지 않는 이메일입니다.");
        }
        if(user.getIsDeleted()){
            throw new UserDeleteException("해당 회원은 탈퇴한 회원입니다.");
        }
        if(!passwordEncoder.matches(userLoginRequest.password(), user.getPassword())){
            throw new PasswordMissmatchException("비밀번호가 일치하지 않습니다.");
        }
        String token =jwtUtil.generateToken(user);
        System.out.println(token);
        return TokenResponse.from(token);
    }

    @Override
    public TokenResponse refreshToken(TokenInfo tokenInfo) {
        User user = userRepository.findById(tokenInfo.id()).orElseThrow();
        String token = jwtUtil.generateToken(user);
        return TokenResponse.from(token);
    }

    @Override
    public UserInfoResponse getUserInfo(TokenInfo token) {
        User byEmail = userRepository.findByEmail(token.email());
        if(byEmail == null) {
            throw new NoUserException("존재하지 않는 유저입니다.");
        }
        return UserInfoResponse.from(byEmail);
    }

    @Override
    public void updateUser(TokenInfo tokenInfo, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findByEmail(tokenInfo.email());
        if(user == null) {
            throw new NoEmailException("존재하지 않는 이메일입니다.");
        }
        if(!Objects.equals(userUpdateRequest.password(), userUpdateRequest.passwordCheck())) {
            throw new PasswordMissmatchException("비밀번호가 일치하지 않습니다.");
        }
        String encodedPassword = passwordEncoder.encode(userUpdateRequest.password());
        user.updateUser(userUpdateRequest.name(),encodedPassword, userUpdateRequest.address(),userUpdateRequest.addressDetail());
        userRepository.save(user);
    }

    @Override
    public void deleteUser(TokenInfo tokenInfo) {
        User user = userRepository.findByEmail(tokenInfo.email());
        if(user == null) {
            throw new NoUserException("존재하지 않는 유저입니다.");
        }
        user.withDrawUser();
        User savedUser = userRepository.save(user);
        KafkaUserDeleteRequest kafkaUserDeleteRequest = new KafkaUserDeleteRequest(savedUser.getUserId());
        kafkaProducer.sendDelete(kafkaUserDeleteRequest, "delete-topic");
    }



}
