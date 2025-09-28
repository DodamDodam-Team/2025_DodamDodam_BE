package com.dodamdodam.dodamdodam.login.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class JoinRequest {
    private String username;
    private String password;
}
