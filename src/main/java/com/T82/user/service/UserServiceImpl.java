package com.T82.user.service;

import com.T82.common_exception.exception.user.DuplicatePhoneNumberException;
import com.T82.common_exception.exception.user.EmailNotFoundException;
import com.T82.common_exception.exception.user.PasswordMismatchException;
import com.T82.common_exception.exception.user.UserNotFoundException;
import com.T82.user.domain.dto.request.*;
import com.T82.user.domain.dto.response.TokenResponse;
import com.T82.user.domain.dto.response.UserInfoResponse;
import com.T82.user.domain.entity.User;
import com.T82.user.domain.repository.UserRepository;
import com.T82.user.global.utils.JwtUtil;
import com.T82.user.global.utils.TokenInfo;
import com.T82.user.kafka.dto.request.KafkaAllowRequest;
import com.T82.user.kafka.dto.request.KafkaUserRequest;
import com.T82.user.kafka.producer.KafkaProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Override
//    @CustomException(ErrorCode.FAILED_SIGNUP)  "회원가입을 실패했습니다."
    public void signUpUser(UserSignUpRequest userSignUpRequest) {
        User byEmail = userRepository.findByEmail(userSignUpRequest.email());
        if(byEmail != null) {
            throw new EmailNotFoundException();
        }
        if (!Objects.equals(userSignUpRequest.password(), userSignUpRequest.passwordCheck()))
            throw new PasswordMismatchException();
        User byPhone = userRepository.findByPhoneNumber(userSignUpRequest.phoneNumber());
        if(byPhone != null) {
            throw new DuplicatePhoneNumberException();
        }
        String encodedPassword = passwordEncoder.encode(userSignUpRequest.password());
        User user = userRepository.save(userSignUpRequest.toEntity(encodedPassword));
        KafkaUserRequest kafkaUserRequest = new KafkaUserRequest(user.getUserId(), user.getEmail(),user.getName(),user.getIsArtist(),user.getProfileUrl());
        kafkaProducer.sendSignUp(kafkaUserRequest, "userTopic");
    }

    @Override
    //    @CustomException(ErrorCode.FAILED_LOGIN)  "로그인을 실패했습니다."
    public TokenResponse loginUser(UserLoginRequest userLoginRequest) {
        User user = userRepository.findByEmail(userLoginRequest.email());
        if(user == null) {
            throw new EmailNotFoundException();
        }
        if(user.getIsDeleted()){
            throw new UserNotFoundException();
        }
        if(!passwordEncoder.matches(userLoginRequest.password(), user.getPassword())){
            throw new PasswordMismatchException();
        }
        String token =jwtUtil.generateToken(user);
        return TokenResponse.from(token);
    }

    @Override
    //    @CustomException(ErrorCode.FAILED_TOKEN)  "토큰 발급에 실패했습니다."
    public TokenResponse refreshToken(TokenInfo tokenInfo) {
        User user = userRepository.findById(tokenInfo.id()).orElseThrow();
        String token = jwtUtil.generateToken(user);
        return TokenResponse.from(token);
    }

    @Override
    //    @CustomException(ErrorCode.FAILED_USER_INFO)  "유저 정보 불러오기를 실패헀습니다."
    public UserInfoResponse getUserInfo(TokenInfo token) {
        User byEmail = userRepository.findByEmail(token.email());
        if(byEmail == null) {
            throw new UserNotFoundException();
        }
        return UserInfoResponse.from(byEmail);
    }

    @Override
    //    @CustomException(ErrorCode.FAILED_UPDATE_USER)  "유저 업데이트 작업에 실패헀습니다."
    public void updateUser(TokenInfo tokenInfo, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findByEmail(tokenInfo.email());
        if(user == null) {
            throw new EmailNotFoundException();
        }
        if(!Objects.equals(userUpdateRequest.password(), userUpdateRequest.passwordCheck())) {
            throw new PasswordMismatchException();
        }
        String encodedPassword = passwordEncoder.encode(userUpdateRequest.password());
        user.updateUser(userUpdateRequest.name(),encodedPassword, userUpdateRequest.address(),userUpdateRequest.addressDetail(),userUpdateRequest.profileUrl());
        userRepository.save(user);
        KafkaUserRequest kafkaUserRequest = new KafkaUserRequest(user.getUserId(), user.getEmail(),user.getName(),user.getIsArtist(),user.getProfileUrl());
        kafkaProducer.sendSignUp(kafkaUserRequest, "userTopic");
    }

    @Override
    //    @CustomException(ErrorCode.FAILED_DELETE_USER)  "유저 삭제 작업에 실패헀습니다."
    public void deleteUser(TokenInfo tokenInfo) {
        User user = userRepository.findByEmail(tokenInfo.email());
        if(user == null) {
            throw new UserNotFoundException();
        }
        user.withDrawUser();
        userRepository.save(user);
        KafkaUserRequest kafkaUserRequest = new KafkaUserRequest(user.getUserId(), user.getEmail(),user.getName(),user.getIsArtist(),user.getProfileUrl());
        kafkaProducer.sendDelete(kafkaUserRequest, "userTopic");
    }

    @Override
    //    @CustomException(ErrorCode.FAILED_SEND_DEVICE)  "디바이스 토큰 전송 작업에 실패헀습니다."
    public void sendDeviceToken(DeviceTokenRequest req,TokenInfo tokenInfo) {
        KafkaAllowRequest kafkaAllowRequest = new KafkaAllowRequest(tokenInfo.id(),req.deviceToken());
        kafkaProducer.sendDeviceToken(kafkaAllowRequest, "deviceTopic");
    }


    //    @CustomException(ErrorCode.FAILED_LOGIN)  "로그인을 실패했습니다."
    public TokenResponse loginOauth(String accessToken, String provider) {
        String url = getProviderUrl(provider);
        log.info("Request URL: " + url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode userInfo = objectMapper.readTree(response.getBody());

            String id = getProviderId(userInfo, provider);
            String name = getProviderName(userInfo, provider);
            String email = getProviderEmail(userInfo, provider);

            User user = userRepository.findByEmail(email);
            if (user == null) {
                user = User.builder()
                        .email(email)
                        .name(name)
                        .provider(provider)
                        .providerId(id)
                        .profileUrl(null)
                        .isDeleted(false)
                        .createdDate(LocalDate.now())
                        .build();
                userRepository.save(user);
                KafkaUserRequest kafkaUserRequest = new KafkaUserRequest(user.getUserId(), user.getEmail(), user.getName(), user.getIsArtist(), user.getProfileUrl());
                kafkaProducer.sendSignUp(kafkaUserRequest, "userTopic");
            } else if (user.getIsDeleted()) {
                throw new UserNotFoundException();
            }

            String token = jwtUtil.generateToken(user);
            log.info("Generated Token: " + token);
            return TokenResponse.from(token);
        } catch (HttpClientErrorException e) {
            log.info("HTTP Status Code: " + e.getStatusCode());
            log.info("Response Body: " + e.getResponseBodyAsString());
            throw e;
        } catch (JsonProcessingException e) {
            log.info("JSON 처리 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("JSON 처리 중 오류 발생", e);
        } catch (Exception e) {
            log.info("예상치 못한 오류 발생: " + e.getMessage());
            throw e;
        }
    }

    private String getProviderUrl(String provider) {
        switch (provider.toLowerCase()) {
            case "kakao":
                return "https://kapi.kakao.com/v2/user/me";
            case "google":
                return "https://www.googleapis.com/userinfo/v2/me";
            default:
                throw new IllegalArgumentException("지원하지 않는 provider입니다: " + provider);
        }
    }

    private String getProviderId(JsonNode userInfo, String provider) {
        switch (provider.toLowerCase()) {
            case "kakao":
                return userInfo.path("id").asText();
            case "google":
                return userInfo.path("id").asText();
            default:
                throw new IllegalArgumentException("지원하지 않는 provider입니다: " + provider);
        }
    }

    private String getProviderName(JsonNode userInfo, String provider) {
        switch (provider.toLowerCase()) {
            case "kakao":
                return userInfo.path("properties").path("nickname").asText();
            case "google":
                return userInfo.path("name").asText();
            default:
                throw new IllegalArgumentException("지원하지 않는 provider입니다: " + provider);
        }
    }

    private String getProviderEmail(JsonNode userInfo, String provider) {
        switch (provider.toLowerCase()) {
            case "kakao":
                return userInfo.path("kakao_account").path("email").asText();
            case "google":
                return userInfo.path("email").asText();
            default:
                throw new IllegalArgumentException("지원하지 않는 provider입니다: " + provider);
        }
    }
}
