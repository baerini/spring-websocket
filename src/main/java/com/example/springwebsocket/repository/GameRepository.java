package com.example.springwebsocket.repository;

import com.example.springwebsocket.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
