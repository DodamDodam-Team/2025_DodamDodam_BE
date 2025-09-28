package com.dodamdodam.dodamdodam.jwt.service;

import com.dodamdodam.dodamdodam.login.entity.User;
import com.dodamdodam.dodamdodam.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " 을 DB에서 찾을 수 없습니다"));
        return createUserDetails(user);
    }

    private UserDetails createUserDetails(User user) {
        return new CustomUserDetails(user);
    }
}