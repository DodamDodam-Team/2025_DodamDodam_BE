package com.dodamdodam.dodamdodam.login.service;

import com.dodamdodam.dodamdodam.jwt.util.JwtUtil;
import com.dodamdodam.dodamdodam.login.dto.JoinRequest;
import com.dodamdodam.dodamdodam.login.dto.LoginRequest;
import com.dodamdodam.dodamdodam.login.entity.User;
import com.dodamdodam.dodamdodam.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void join(JoinRequest joinRequest) {
        if (userRepository.findByUsername(joinRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        String encryptedPassword = passwordEncoder.encode(joinRequest.getPassword());

        User user = User.builder()
                .username(joinRequest.getUsername())
                .password(encryptedPassword)
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    }

    public String login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("아이디가 존재하지 않습니다."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return jwtUtil.createToken(user.getUsername());
    }
}