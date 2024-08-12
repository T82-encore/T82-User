package com.T82.user.api;

import com.T82.user.domain.entity.User;
import com.T82.user.global.utils.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOauth2UserDetails customOauth2UserDetails = (CustomOauth2UserDetails) authentication.getPrincipal();
        customOauth2UserDetails.getName();
        customOauth2UserDetails.getEmail();

        User user = User.builder()
                .userId(UUID.fromString(customOauth2UserDetails.getName()))
                .email(customOauth2UserDetails.getEmail())
                .build();
        System.out.println(user.getUserId());
        String token = jwtUtil.generateToken(user);

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter writer = response.getWriter();
        writer.write("{");
        writer.write("\"token\":\"" + token + "\",");
        writer.write("\"tokenType\":\"Bearer\"");
        writer.write("}");
        writer.flush();
    }
}
