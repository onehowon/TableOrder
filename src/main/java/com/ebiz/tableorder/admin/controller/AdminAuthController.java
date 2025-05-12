package com.ebiz.tableorder.admin.controller;

import com.ebiz.tableorder.admin.component.JwtTokenProvider;
import com.ebiz.tableorder.admin.dto.LoginRequest;
import com.ebiz.tableorder.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAuthController {
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<Map<String,String>>> login(
            @RequestBody LoginRequest req) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        String token = tokenProvider.createToken(req.getEmail());
        return ResponseEntity.ok(CommonResponse.success(
                Map.of("accessToken", token),
                "로그인 성공"
        ));
    }
}