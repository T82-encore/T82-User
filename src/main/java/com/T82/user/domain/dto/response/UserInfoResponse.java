package com.T82.user.domain.dto.response;

import com.T82.user.domain.entity.User;

public record UserInfoResponse(
        String name,
        String address,
        String addressDetail,
        String PhoneNumber
) {
    public static UserInfoResponse from(User user){
        return new UserInfoResponse(user.getName(), user.getAddress(), user.getAddressDetail(), user.getPhoneNumber());
    }
}
