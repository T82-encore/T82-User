package com.T82.user.api;

import com.T82.user.domain.entity.User;
import com.T82.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}",oAuth2User.getAttributes());

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = null;

        // 뒤에 진행할 다른 소셜 서비스 로그인을 위해 구분 => 구글
        if(provider.equals("google")){
            log.info("구글 로그인");
            oAuth2UserInfo = new GoogleUserDetails(oAuth2User.getAttributes());

        }
        else if (provider.equals("kakao")) {
            log.info("카카오 로그인");
            oAuth2UserInfo = new KakaoUserDetails(oAuth2User.getAttributes());
        }

        String username = oAuth2UserInfo.getProvider() + " " + oAuth2UserInfo.getProviderId();
//        String email = oAuth2UserInfo.getEmail();
//        User byEmail = userRepository.findByEmail(email);
//        if(byEmail == null){
//            user = User.builder()
//                    .email(email)
//                    .name(name)
//                    .provider(provider)
//                    .isDeleted(false)
//                    .createdDate(LocalDate.now())
//                    .providerId(providerId)
//                    .build();
//            userRepository.save(user);
//        }
//
//
//        UserDTO userDTO = new UserDTO();
//        userDTO.setUsername(username);
//        userDTO.setName(oAuth2UserInfo.getName());
//
//
//
//        return new CustomOauth2UserDetails(userDTO);




        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();
        String loginId = provider + "_" + providerId;
        String name = oAuth2UserInfo.getName();

        User findEmail = userRepository.findByEmail(email);
        User user;

        if (findEmail == null) {
            user = User.builder()
                    .email(email)
                    .name(name)
                    .provider(provider)
                    .isDeleted(false)
                    .createdDate(LocalDate.now())
                    .providerId(providerId)
                    .build();
            userRepository.save(user);
        } else{
            user = findEmail;
        }

        return new CustomOauth2UserDetails(user, oAuth2User.getAttributes());
    }

}
