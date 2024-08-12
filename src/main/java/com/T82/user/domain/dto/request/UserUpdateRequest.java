package com.T82.user.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record UserUpdateRequest(
        @NotBlank
        @Size(min = 2, message = "최소 2자 이상 입력해주세요.")
        @Pattern(regexp = "^([A-Za-z가-힣]{2,})+$", message = "올바른 이름 형식을 입력해주세요.")
        String name,
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,15}$",
                message = "문자, 숫자, 특수문자를 최소 1개 이상 포함하여 최소 8자, 최대 15자 사이로 입력해주세요.")
        String password,
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,15}$",
                message = "문자, 숫자, 특수문자를 최소 1개 이상 포함하여 최소 8자, 최대 15자 사이로 입력해주세요.")
        String passwordCheck,
        String address,
        String addressDetail

) {

}
