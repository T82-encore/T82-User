package com.T82.user.service;

import com.T82.user.domain.dto.request.*;
import com.T82.user.domain.dto.response.TokenResponse;
import com.T82.user.domain.dto.response.UserInfoResponse;
import com.T82.user.domain.entity.User;
import com.T82.user.domain.repository.UserRepository;
import com.T82.user.exception.*;
import com.T82.user.global.utils.JwtUtil;
import com.T82.user.global.utils.TokenInfo;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SignatureException;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRepository userRepositoryMock;
    @Autowired
    private UserService userService;
    @InjectMocks
    private UserServiceImpl userServiceMock;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @Mock
    private JwtUtil jwtUtilMock;


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
                    "어딘가-123",
                    "어딘가-123",
                    true,
                    "https://via.placeholder.com/150"
            );
            int lengthBefore = userRepository.findAll().size();
            //when
            userService.signUpUser(userSignUpRequest);
            //then
            assertThat(userRepository.findAll().size()).isEqualTo(lengthBefore + 1);
        }

        @Test
        void 실패_이미_존재하는_이메일(){
            //given
            UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
                    "test@naver.com",
                    "1234",
                    "1234",
                    "테스트",
                    LocalDate.now(),
                    "010-1234-5678",
                    "어딘가-123",
                    "어딘가-123",
                    true,
                    "https://via.placeholder.com/150"
            );
            userService.signUpUser(userSignUpRequest);
            //when & then
            DuplicateEmailException exception =
                    assertThrows(DuplicateEmailException.class, () -> userService.signUpUser(userSignUpRequest));
            assertThat("이미 존재하는 이메일입니다.").isEqualTo(exception.getMessage());
        }

        @Test
        void 실패_비밀번호_불일치(){
            //given
            UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
                    "test@naver.com",
                    "1234",
                    "5678",
                    "테스트",
                    LocalDate.now(),
                    "010-1234-5678",
                    "어딘가-123",
                    "어딘가-123",
                    true,
                    "https://via.placeholder.com/150"
            );

            //when & then
            PasswordMissmatchException exception = assertThrows(PasswordMissmatchException.class, () -> {
                userService.signUpUser(userSignUpRequest);
            });
            assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
        }

        @Test
        void 실패_이미_존재하는_휴대폰번호(){
            //given
            UserSignUpRequest userSignUpRequest1 = new UserSignUpRequest(
                    "test1@naver.com",
                    "1234",
                    "1234",
                    "테스트1",
                    LocalDate.now(),
                    "010-1234-5678",
                    "어딘가-123",
                    "어딘가-123",
                    true,
                    "https://via.placeholder.com/150"
            );
            userService.signUpUser(userSignUpRequest1);

            UserSignUpRequest userSignUpRequest2 = new UserSignUpRequest(
                    "test2@naver.com",
                    "1234",
                    "1234",
                    "테스트2",
                    LocalDate.now(),
                    "010-1234-5678",
                    "어딘가-123",
                    "어딘가-123",
                    true,
                    "https://via.placeholder.com/150"
            );

            //when & then
            DuplicateNumberException exception = assertThrows(DuplicateNumberException.class, () -> {
                userService.signUpUser(userSignUpRequest2);
            });
            assertEquals("이미 존재하는 휴대폰 번호입니다.", exception.getMessage());
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
                    "어딘가-123",
                    "어딘가-123",
                    true,
                    "https://via.placeholder.com/150"
            );
            userService.signUpUser(userSignUpRequest);

            UserLoginRequest userLoginRequest = new UserLoginRequest(
                    "test@naver.com","1234"
            );
            //when & then
            userService.loginUser(userLoginRequest);

        }

        @Test
        void 실패_아이디가_틀린경우() {
            //given
            UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
                    "test@naver.com",
                    "1234",
                    "1234",
                    "테스트",
                    LocalDate.now(),
                    "010-1234-5678",
                    "어딘가-123",
                    "어딘가-123",
                    true,
                    "https://via.placeholder.com/150"
            );
            userService.signUpUser(userSignUpRequest);

            UserLoginRequest userLoginRequest = new UserLoginRequest(
                    "wrong@naver.com", "1234"
            );
            //when & then
            NoEmailException exception = assertThrows(NoEmailException.class, () -> {
                userService.loginUser(userLoginRequest);
            });
            assertEquals("존재하지 않는 이메일입니다.", exception.getMessage());
        }

        @Test
        void 실패_비밀번호가_틀린경우() {
            //given
            UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
                    "test@naver.com",
                    "1234",
                    "1234",
                    "테스트",
                    LocalDate.now(),
                    "010-1234-5678",
                    "어딘가-123",
                    "어딘가-123",
                    true,
                    "https://via.placeholder.com/150"
            );
            userService.signUpUser(userSignUpRequest);

            UserLoginRequest userLoginRequest = new UserLoginRequest(
                    "test@naver.com", "wrongPassword"
            );
            //when & then
            PasswordMissmatchException exception = assertThrows(PasswordMissmatchException.class, () -> {
                userService.loginUser(userLoginRequest);
            });
            assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
        }
    }

    @Nested
    @Transactional
    class 회원_정보_불러오기{
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
                    "어딘가-123",
                    "어딘가-123",
                    true,
                    "https://via.placeholder.com/150"
            );
            userService.signUpUser(userSignUpRequest);
            UserLoginRequest userLoginRequest = new UserLoginRequest(
                    "test@naver.com","1234"
            );
            TokenResponse tokenResponse = userService.loginUser(userLoginRequest);
            TokenInfo tokenInfo = jwtUtil.parseToken(tokenResponse.token());

            //when
            UserInfoResponse userInfo = userService.getUserInfo(tokenInfo);

            //then
            assertThat(userSignUpRequest.name()).isEqualTo(userInfo.name());
            assertThat(userSignUpRequest.address()).isEqualTo(userInfo.address());
            assertThat(userSignUpRequest.addressDetail()).isEqualTo(userInfo.addressDetail());
            assertThat(userSignUpRequest.phoneNumber()).isEqualTo(userInfo.phoneNumber());
        }

        @Test
        void 실패_존재하지_않는_사용자() {
            //given
            UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
                    "test@naver.com",
                    "1234",
                    "1234",
                    "테스트",
                    LocalDate.now(),
                    "010-1234-5678",
                    "어딘가-123",
                    "어딘가-123",
                    true,
                    "https://via.placeholder.com/150"
            );
            userService.signUpUser(userSignUpRequest);
            UserLoginRequest userLoginRequest = new UserLoginRequest(
                    "test@naver.com", "1234"
            );
            TokenResponse tokenResponse = userService.loginUser(userLoginRequest);

            // 존재하지 않는 이메일을 사용하여 TokenInfo 객체 생성
            TokenInfo nonExistentUserTokenInfo = new TokenInfo(
                    UUID.randomUUID(), "nonexistent@naver.com"
            );

            //when
            NoUserException exception = assertThrows(NoUserException.class, () -> {
                userService.getUserInfo(nonExistentUserTokenInfo);
            });

            //then
            assertEquals("존재하지 않는 유저입니다.", exception.getMessage());
        }

    }

    @Nested
    @Transactional
    class 회원_정보_수정{
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
                    "어딘가-123",
                    "어딘가-123",
                    true,
                    "https://via.placeholder.com/150"
            );
            userService.signUpUser(userSignUpRequest);
            UserLoginRequest userLoginRequest = new UserLoginRequest(
                    "test@naver.com","1234"
            );
            TokenResponse tokenResponse = userService.loginUser(userLoginRequest);
            TokenInfo tokenInfo = jwtUtil.parseToken(tokenResponse.token());

            UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                    "홍길동",
                    "1234",
                    "1234",
                    "바뀐주소-1234",
                    "바뀐상세주소-123",
                    "https://via.placeholder.com/200"
            );
            //when
            userService.updateUser(tokenInfo, userUpdateRequest);
            //then
            User byEmail = userRepository.findByEmail(jwtUtil.parseToken(tokenResponse.token()).email());
            assertThat(byEmail.getName()).isEqualTo("홍길동");
            passwordEncoder.matches("1234", byEmail.getPassword());
            assertThat(byEmail.getAddress()).isEqualTo("바뀐주소-1234");
            assertThat(byEmail.getAddressDetail()).isEqualTo("바뀐상세주소-123");


        }

        @Test
        void 실패_비밀번호_불일치() {
            // given
            UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
                    "test@naver.com",
                    "1234",
                    "1234",
                    "테스트",
                    LocalDate.now(),
                    "010-1234-5678",
                    "어딘가-123",
                    "어딘가-123",
                    true,
                    "https://via.placeholder.com/150"
            );
            userService.signUpUser(userSignUpRequest);

            UserLoginRequest userLoginRequest = new UserLoginRequest(
                    "test@naver.com", "1234"
            );
            TokenResponse tokenResponse = userService.loginUser(userLoginRequest);
            TokenInfo tokenInfo = jwtUtil.parseToken(tokenResponse.token());
            System.out.println(tokenResponse.token());
            UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                    "홍길동",
                    "1111",
                    "111",
                    "바뀐주소",
                    "바뀐 상세주소",
                    "https://via.placeholder.com/200"
            );

            // when & then
            PasswordMissmatchException exception = assertThrows(PasswordMissmatchException.class, () -> {
                userService.updateUser(tokenInfo, userUpdateRequest);
            });
            assertThat("비밀번호가 일치하지 않습니다.").isEqualTo(exception.getMessage());
        }
    }

    @Nested
    @Transactional
    class 회원_탈퇴{
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
                    "어딘가-123",
                    "어딘가-123",
                    true,
                    "https://via.placeholder.com/150"
            );
            userService.signUpUser(userSignUpRequest);
            UserLoginRequest userLoginRequest = new UserLoginRequest(
                    "test@naver.com","1234"
            );
            TokenResponse tokenResponse = userService.loginUser(userLoginRequest);
            TokenInfo tokenInfo = jwtUtil.parseToken(tokenResponse.token());

            //when
            userService.deleteUser(tokenInfo);
            User byEmail = userRepository.findByEmail(jwtUtil.parseToken(tokenResponse.token()).email());

            //then
            assertThat(byEmail.getIsDeleted()).isEqualTo(true);
        }

        @Test
        void 실패_존재하지_않는_사용자(){
            //given
            UserSignUpRequest userSignUpRequest = new UserSignUpRequest(
                    "test@naver.com",
                    "1234",
                    "1234",
                    "테스트",
                    LocalDate.now(),
                    "010-1234-5678",
                    "어딘가-123",
                    "어딘가-123",
                    true,
                    "https://via.placeholder.com/150"
            );
            userService.signUpUser(userSignUpRequest);
            UserLoginRequest userLoginRequest = new UserLoginRequest(
                    "test@naver.com", "1234"
            );
            TokenResponse tokenResponse = userService.loginUser(userLoginRequest);
            TokenInfo tokenInfo = jwtUtil.parseToken(tokenResponse.token());

            TokenInfo nonExistentUserTokenInfo = new TokenInfo(
                    UUID.randomUUID(), "nonexistent@naver.com"
            );

            //when
            NoUserException exception = assertThrows(NoUserException.class, () -> {
                userService.deleteUser(nonExistentUserTokenInfo);
            });

            //then
            assertEquals("존재하지 않는 유저입니다.", exception.getMessage());
        }
    }
}