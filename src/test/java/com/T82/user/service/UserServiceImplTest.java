package com.T82.user.service;

import com.T82.user.domain.dto.request.*;
import com.T82.user.domain.dto.response.UserInfoResponse;
import com.T82.user.domain.entity.User;
import com.T82.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Nested
    @Transactional
    class 회원가입{
        @Test
        void 성공(){
            //given
            UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
                    "test@naver.com",
                    "1234",
                    "1234",
                    "테스트",
                    LocalDate.now(),
                    "010-1234-5678",
                    "어딘가 123",
                    "어딘가 123"
            );
            int lengthBefore = userRepository.findAll().size();
            //when
            userService.signUpUser(userSignUpRequest);
            //then
            assertEquals(userRepository.findAll().size(), lengthBefore + 1);
        }
    }

    @Nested
    @Transactional
    class 로그인{
        @Test
        void 성공(){
            //given
            UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
                    "test@naver.com",
                    "1234",
                    "1234",
                    "테스트",
                    LocalDate.now(),
                    "010-1234-5678",
                    "어딘가 123",
                    "어딘가 123"
            );
            userService.signUpUser(userSignUpRequest);

            UserLoginRequest userLoginRequest = new UserLoginRequest(
                    "test@naver.com","1234"
            );
            //when & then
            userService.loginUser(userLoginRequest);
        }
    }

    @Nested
    @Transactional
    class 회원_탈퇴{
        @Test
        void 성공(){
            //given
            User user = new User(UUID.randomUUID(),"test@naver.com","1234","테스트",
                LocalDate.now(),"010-1234-5678","테스트","테스트",
                    false,LocalDate.now(),null);
            userRepository.save(user);
//            UserWithDrawRequest userWithDrawRequest = new UserWithDrawRequest("test@naver.com");
            //when
            userService.deleteUser(userWithDrawRequest);
            //then
            User byEmail = userRepository.findByEmail("test@naver.com");
            assertEquals(true,byEmail.getIsDeleted());
        }
    }

    @Nested
    @Transactional
    class 회원_정보_불러오기{
        @Test
        void 성공(){
            //given
            User user = new User(UUID.randomUUID(),"test@naver.com","1234","이름이요",
                    LocalDate.now(),"010-1234-5678","주소","상세주소",
                    false,LocalDate.now(),null);
            userRepository.save(user);
//            UserInfoRequest userInfoRequest = new UserInfoRequest("test@naver.com");
            //when
            UserInfoResponse userInfo = userService.getUserInfo(userInfoRequest);
            User byEmail = userRepository.findByEmail("test@naver.com");
            //then
            assertEquals(byEmail.getName(),userInfo.name());
        }
    }

    @Nested
    @Transactional
    class 회원_정보_수정{
        @Test
        void 성공(){
            //given
            User user = new User(UUID.randomUUID(),"test@naver.com","1234","이름이요",
                    LocalDate.now(),"010-1234-5678","주소","상세주소",
                    false,LocalDate.now(),null);
            userRepository.save(user);
            UserUpdateRequest userUpdateRequest =
                    new UserUpdateRequest("이름이요","1111","1111",
                            "바뀐주소","바뀐상세주소");
            //when
            userService.updateUser(userUpdateRequest);
            User byEmail = userRepository.findByEmail("test@naver.com");
            //then
            assertEquals("이름이요",byEmail.getName());
            assertEquals("1111",byEmail.getPassword());
            assertEquals("바뀐주소",byEmail.getAddress());
            assertEquals("바뀐상세주소",byEmail.getAddressDetail());
            assertEquals(LocalDate.now(),byEmail.getModifiedDate());
        }
    }
}