package com.example.springwebsocket.domain.dto;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMemberDto {
    private String loginId;
    private String password;
    private String username;
}
