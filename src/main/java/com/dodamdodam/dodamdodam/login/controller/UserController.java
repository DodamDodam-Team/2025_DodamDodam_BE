package com.dodamdodam.dodamdodam.login.controller;

import com.dodamdodam.dodamdodam.login.dto.JoinRequest;
import com.dodamdodam.dodamdodam.login.dto.LoginRequest;
import com.dodamdodam.dodamdodam.login.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody JoinRequest joinRequest) {
        userService.join(joinRequest);
        return ResponseEntity.ok("회원가입 성공!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String jwt = userService.login(loginRequest);
        return ResponseEntity.ok(jwt);
    }

    @GetMapping("/info")
    public ResponseEntity<String> getUserInfo(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok("사용자 이름: " + username);
    }
}