package com.T82.user.domain.repository;

import com.T82.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>{
    User findByPhoneNumber(String phoneNumber);
    User findByEmail(String email);
    //추후 토큰 관련된 값으로 변경 필요
    User findByUserId(UUID userId);
}

