package com.dodamdodam.dodamdodam.login.controller;

import com.dodamdodam.dodamdodam.login.dto.JoinRequest;
import com.dodamdodam.dodamdodam.login.dto.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "User API", description = "사용자 회원가입 및 로그인 관련 API")
public interface UserDocsController {

    @Operation(summary = "회원가입", description = "사용자 정보를 받아 회원가입을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 아이디입니다. (Conflict)")
    })
    ResponseEntity<String> join(@RequestBody JoinRequest joinRequest);


    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인을 진행하고 JWT를 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공. JWT 토큰을 Body에 반환합니다.", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 일치하지 않습니다. (Unauthorized)")
    })
    ResponseEntity<String> login(@RequestBody LoginRequest loginRequest);


    @Operation(summary = "사용자 정보 조회", description = "인증된 사용자의 이름을 조회합니다. **(JWT 토큰 필요)**")
    @SecurityRequirement(name = "bearerAuth") // Swagger에서 자물쇠 아이콘을 표시하고, 인증 토큰을 요구
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다. (Unauthorized)"),
            @ApiResponse(responseCode = "403", description = "접근 권한이 없습니다. (Forbidden)")
    })
    ResponseEntity<String> getUserInfo(Authentication authentication);
}