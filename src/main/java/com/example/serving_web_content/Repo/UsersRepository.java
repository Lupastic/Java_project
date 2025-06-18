package com.example.serving_web_content.Repo;

import com.example.serving_web_content.models.Users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);
    Page<Users> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
