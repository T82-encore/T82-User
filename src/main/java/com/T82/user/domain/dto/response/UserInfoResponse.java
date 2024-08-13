package com.T82.user.domain.dto.response;

import com.T82.user.domain.entity.User;


public record UserInfoResponse(
        String name,
        String email,
        String birthDate,
        String address,
        String addressDetail,
        String phoneNumber,
        String profileUrl
) {
    public static UserInfoResponse from(User user){
        return new UserInfoResponse(user.getName(), user.getEmail(),user.getBirthDate().toString(),
                user.getAddress(), user.getAddressDetail(), user.getPhoneNumber(), user.getProfileUrl());
    }
}
