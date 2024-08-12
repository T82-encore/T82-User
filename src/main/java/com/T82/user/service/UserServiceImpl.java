package com.T82.user.service;

import com.T82.user.domain.dto.request.*;
import com.T82.user.domain.dto.response.TokenResponse;
import com.T82.user.domain.dto.response.UserInfoResponse;
import com.T82.user.domain.entity.User;
import com.T82.user.domain.repository.UserRepository;
import com.T82.user.exception.*;
import com.T82.user.global.utils.JwtUtil;
import com.T82.user.global.utils.TokenInfo;
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
        KafkaUserRequest kafkaUserRequest = new KafkaUserRequest(user.getUserId(), user.getEmail(),user.getName(),user.getIsArtist());
        kafkaProducer.sendSignUp(kafkaUserRequest, "userTopic");
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
        userRepository.save(user);
        KafkaUserRequest kafkaUserRequest = new KafkaUserRequest(user.getUserId(), user.getEmail(),user.getName(),user.getIsArtist());
        kafkaProducer.sendDelete(kafkaUserRequest, "userTopic");
    }

    public TokenResponse kakaoLogin(String accessToken){
        String url = "https://kapi.kakao.com/v2/user/me";
        log.info("Request URL: " + url);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            log.info("response : " + response);
            JsonNode userInfo = objectMapper.readTree(response.getBody());

            String id = userInfo.path("id").asText();
            String nickname = userInfo.path("properties").path("nickname").asText();
            String email = userInfo.path("kakao_account").path("email").asText();

            User user = userRepository.findByEmail(email);
            if(user == null) {
                user = User.builder()
                        .email(email)
                        .name(nickname)
                        .provider("kakao")
                        .providerId(id)
                        .isDeleted(false)
                        .createdDate(LocalDate.now())
                        .build();
                userRepository.save(user);
                KafkaUserRequest kafkaUserRequest = new KafkaUserRequest(user.getUserId(), user.getEmail(),user.getName(),user.getIsArtist());
                System.out.println("만들어진거:" + kafkaUserRequest);
                kafkaProducer.sendSignUp(kafkaUserRequest, "userTopic");

            }else if (user.getIsDeleted()) {
                throw new UserDeleteException("해당 회원은 탈퇴한 회원입니다.");
            }
            String token = jwtUtil.generateToken(user);
            log.info("Generated Token: " + token);
            return TokenResponse.from(token);
        } catch (HttpClientErrorException e) {
            log.info("HTTP Status Code: " + e.getStatusCode());
            log.info("Response Body: " + e.getResponseBodyAsString());
            throw e;
        }catch (JsonProcessingException e) {
            log.info("JSON 처리 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("JSON 처리 중 오류 발생", e);
        }
        catch (Exception e) {
            log.info("예상치 못한 오류 발생: " + e.getMessage());
            throw e;
        }
    }

    public TokenResponse googleLogin(String accessToken) {
        String url = "https://www.googleapis.com/userinfo/v2/me";
        log.info("Request URL: " + url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode userInfo = objectMapper.readTree(response.getBody());

            String id = userInfo.path("id").asText();
            String name = userInfo.path("name").asText();
            String email = userInfo.path("email").asText();

            User user = userRepository.findByEmail(email);
            if (user == null) {
                user = User.builder()
                        .email(email)
                        .name(name)
                        .provider("google")
                        .providerId(id)
                        .isDeleted(false)
                        .createdDate(LocalDate.now())
                        .build();
                userRepository.save(user);
                KafkaUserRequest kafkaUserRequest = new KafkaUserRequest(user.getUserId(), user.getEmail(), user.getName(), user.getIsArtist());
                System.out.println("kafka : "+kafkaUserRequest);
                kafkaProducer.sendSignUp(kafkaUserRequest, "userTopic");
            } else if (user.getIsDeleted()) {
                throw new UserDeleteException("해당 회원은 탈퇴한 회원입니다.");
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



}
