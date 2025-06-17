package com.example.serving_web_content.service.user;

import com.example.serving_web_content.DTO.user.CreateUserRequestDto;
import com.example.serving_web_content.models.Users;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public Users toEntity(CreateUserRequestDto dto) {
        Users users = new Users();
        users.setUsername(dto.getUsername());
        users.setPassword(dto.getPassword());
        users.setEmail(dto.getEmail());
        return users;
    }
}
