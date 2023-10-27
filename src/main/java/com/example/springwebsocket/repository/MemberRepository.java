package com.example.springwebsocket.repository;

import com.example.springwebsocket.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
