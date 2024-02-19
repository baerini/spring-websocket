package com.example.springwebsocket.repository;

import com.example.springwebsocket.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByLoginId(String loginId);
    Member findByLoginIdAndPassword(String loginId, String password);
    Member findByUsername(String username);
}
