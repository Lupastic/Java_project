package com.example.serving_web_content.service.user;

import com.example.serving_web_content.DTO.user.ChangeDetailsFormData;
import com.example.serving_web_content.DTO.user.ChangeUserDetailsDto;
import com.example.serving_web_content.DTO.user.CreateUserRequestDto;
import com.example.serving_web_content.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface UserInterface {
    UserOperationResult createUser(CreateUserRequestDto dto);
    ChangeDetailsFormData getChangeUserDetailsDto(Long targetUserIdParam, UserDetails currentUserDetails);
    UserOperationResult deleteUser(Long userID, String performedBy);
    UserOperationResult updateUserDetails(ChangeUserDetailsDto dto, String currentUsername);
    UserOperationResult updateUserAdmin(ChangeUserDetailsDto dto, Long userID, String username);

    Page<Users> findUsers(Pageable pageable, String usernameSearchTerm);

    Optional<Users> findByUsername(String username);
    Optional<Users> findById(Long id);
    Page<Users> findAll(Pageable pageable);
    List<Users> findAllUsers();
}
