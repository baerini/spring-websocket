package com.example.springwebsocket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String loginId;
    private String password;
    private String username;
    private int rating;
    private int win;
    private int lose;

    public Member(String loginId, String password, String username) {
        this.loginId = loginId;
        this.password = password;
        this.username = username;
        this.rating = 1200;
        this.win = 0;
        this.lose = 0;
    }
}
