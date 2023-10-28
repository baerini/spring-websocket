package com.example.springwebsocket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String username;
    private String loginId;
    private String password;

    public Member() {
    }

    public Member(String username, String loginId, String password) {
        this.username = username;
        this.loginId = loginId;
        this.password = password;
    }
}
