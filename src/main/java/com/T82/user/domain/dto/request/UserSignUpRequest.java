package com.T82.user.domain.dto.request;

import com.T82.user.domain.entity.User;
import jakarta.validation.constraints.*;

import java.util.Date;

public record UserSignUpRequest(
        @NotBlank
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,
        @NotBlank
        @Size(min = 8, max = 15, message = "최소 8자, 최대 15자 사이로 입력해주세요.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]$",message = "문자, 숫자, 특수문자를 최소 1개 이상 입력해주세요.")
        String password,
        @NotBlank
        @Size(min = 8, max = 15, message = "최소 8자, 최대 15자 사이를 입력해주세요.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]$",message = "문자, 숫자, 특수문자를 최소 1개 이상 입력해주세요.")
        String passwordCheck,
        @NotBlank
        @Max(value = 100) @Pattern(regexp = "^([A-Za-z가-힣]{2,})+", message = "올바른 이름 형식을 입력해주세요.")
        String name,
        @NotBlank
        @PastOrPresent(message = "올바른 날짜 형식을 입력해주세요.")
        Date birthDate,
        @NotBlank
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 맞지 않습니다. xxx-xxxx-xxxx")
        String phoneNumber,
        String address,
        String addressDetail
) {
    public User toEntity(UserSignUpRequest userSignUpRequest){
        return User.builder()
                .email(email)
                .password(password)
                .name(name)
                .birthDate(birthDate)
                .phoneNumber(phoneNumber)
                .address(address)
                .addressDetail(addressDetail)
                .build();
    }
}
