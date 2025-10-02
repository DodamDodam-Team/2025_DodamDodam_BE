package com.dodamdodam.dodamdodam.jwt.filter;

import com.dodamdodam.dodamdodam.jwt.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // ✅ 1. import 추가
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j // ✅ 2. 어노테이션 추가
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        // ✅ 3. 요청 정보와 Authorization 헤더를 로그로 출력
        log.info(">>>>> Request to {}: Authorization Header: {}", request.getRequestURI(), authorizationHeader);

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            if (jwtUtil.validateToken(token)) {
                // ✅ 4. 토큰이 유효할 경우 로그 출력
                log.info(">>>>> Token is valid.");
                String username = jwtUtil.getUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // ✅ 5. 사용자 정보와 권한을 로그로 출력
                log.info(">>>>> User '{}' found with authorities: {}", username, userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // ✅ 6. 토큰이 유효하지 않을 경우 로그 출력
                log.warn(">>>>> Token is NOT valid.");
            }
        } else {
            // ✅ 7. 헤더에 토큰이 없을 경우 로그 출력
            log.warn(">>>>> No Bearer token found in header.");
        }

        filterChain.doFilter(request, response);
    }
}