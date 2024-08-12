package com.T82.user.domain.dto.request;

import com.T82.user.domain.entity.User;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserSignUpRequest(
        @NotBlank
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,15}$", message = "문자, 숫자, 특수문자를 최소 1개 이상 포함하여 최소 8자, 최대 15자 사이로 입력해주세요.")
        String password,
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,15}$", message = "문자, 숫자, 특수문자를 최소 1개 이상 포함하여 최소 8자, 최대 15자 사이로 입력해주세요.")
        String passwordCheck,
        @NotBlank
        @Size(min = 2, message = "최소 2자 이상 입력해주세요.")
        @Pattern(regexp = "^([A-Za-z가-힣]{2,})+$", message = "올바른 이름 형식을 입력해주세요.")
        String name,
        @NotNull(message = "공백일 수 없습니다.")
        @PastOrPresent(message = "올바른 날짜 형식을 입력해주세요.")
        LocalDate birthDate,
        @NotBlank
        @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 맞지 않습니다. xxx-xxxx-xxxx")
        String phoneNumber,
        String address,
        String addressDetail,
        Boolean isArtist
) {
        public User toEntity(String encodedPassword){
                return User.builder()
                        .email(email)
                        .password(encodedPassword)
                        .name(name)
                        .birthDate(birthDate)
                        .phoneNumber(phoneNumber)
                        .address(address)
                        .addressDetail(addressDetail)
                        .isDeleted(false)
                        .createdDate(LocalDate.now())
                        .isArtist(isArtist != null ? isArtist : false)
                        .build();
        }
}