package com.example.serving_web_content.service.user;

import com.example.serving_web_content.DTO.user.ChangeDetailsFormData;
import com.example.serving_web_content.DTO.user.ChangeUserDetailsDto;
import com.example.serving_web_content.DTO.user.CreateUserRequestDto;
import com.example.serving_web_content.Repo.CityRepository;
import com.example.serving_web_content.Repo.UsersRepository;
import com.example.serving_web_content.models.City;
import com.example.serving_web_content.models.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class usersService implements UserInterface {

    private final UsersRepository usersRepository;
    private final CityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    public usersService(UsersRepository usersRepository, CityRepository cityRepository,
                        PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.usersRepository = usersRepository;
        this.cityRepository = cityRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserOperationResult createUser(CreateUserRequestDto dto) {
        if (dto == null || dto.getUsername() == null || dto.getUsername().isBlank() ||
                dto.getPassword() == null || dto.getPassword().isBlank()) {
            return UserOperationResult.failure(UserOperationResult.Status.INVALID_DATA, "Имя пользователя и пароль обязательны.");
        }

        Users userToCreate = userMapper.toEntity(dto);
        userToCreate.setRole("USER");
        userToCreate.setPassword(passwordEncoder.encode(dto.getPassword()));
        Users savedUser = usersRepository.save(userToCreate);
        return UserOperationResult.success(savedUser.getId());
    }

    private UserOperationResult applyUserDetailsUpdate(Users userToUpdate, ChangeUserDetailsDto dto) {
        userToUpdate.setUsername(dto.getNewUsername());
        userToUpdate.setEmail(dto.getEmail());

        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
                return UserOperationResult.failure(UserOperationResult.Status.INVALID_DATA, "Пароли не совпадают");
            }
            userToUpdate.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        if (dto.getCityId() != null) {
            Optional<City> cityOpt = cityRepository.findById(dto.getCityId());
            cityOpt.ifPresent(userToUpdate::setCity);
        }

        usersRepository.save(userToUpdate);
        return UserOperationResult.success(userToUpdate.getId());
    }

    @Override
    @Transactional
    public UserOperationResult updateUserDetails(ChangeUserDetailsDto dto, String currentUsername) {
        Users userToUpdate = usersRepository.findByUsername(currentUsername);
        if (userToUpdate == null) {
            return UserOperationResult.failure(UserOperationResult.Status.NOT_FOUND, "Текущий пользователь не найден");
        }
        return applyUserDetailsUpdate(userToUpdate, dto);
    }

    @Override
    @Transactional
    public UserOperationResult updateUserAdmin(ChangeUserDetailsDto dto, Long userID, String username) {
        Optional<Users> userOpt = usersRepository.findById(userID);
        Users userToUpdate = userOpt.get();
        return applyUserDetailsUpdate(userToUpdate, dto);
    }
        @Override
        public Page<Users> findUsers(Pageable pageable, String usernameSearchTerm) {
        if (StringUtils.hasText(usernameSearchTerm)) {
            return usersRepository.findByUsernameContainingIgnoreCase(usernameSearchTerm.trim(), pageable);
        } else {
            return usersRepository.findAll(pageable);
        }
    }
    public Optional<Users> findByUsername(String username) {
        Users user = usersRepository.findByUsername(username);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<Users> findById(Long id) {
        return usersRepository.findById(id);
    }

    @Override
    public Page<Users> findAll(Pageable pageable) {
        return usersRepository.findAll(pageable);
    }

    @Override
    public List<Users> findAllUsers() {
        return usersRepository.findAll();
    }
    @Override
    public ChangeDetailsFormData getChangeUserDetailsDto(Long targetUserIdParam, UserDetails currentUserDetails) {
        ChangeDetailsFormData formData = new ChangeDetailsFormData();

        String currentUsername = currentUserDetails.getUsername();
        Users currentUserEntity = usersRepository.findByUsername(currentUsername);

        boolean isAdmin = currentUserDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ADMIN_ROLE));
        formData.setCurrentUserAdmin(isAdmin);

        Users targetUser;
        if (targetUserIdParam == null || (currentUserEntity != null && targetUserIdParam.equals(currentUserEntity.getId()))) {
            targetUser = currentUserEntity;
        } else {
            if (!isAdmin) {
                targetUser = currentUserEntity;
            } else {
                Optional<Users> targetOpt = usersRepository.findById(targetUserIdParam);
                targetUser = targetOpt.orElse(currentUserEntity);
            }
        }

        if (targetUser == null) {
            return null;
        }

        ChangeUserDetailsDto dto = new ChangeUserDetailsDto();
        dto.setTargetUserId(targetUser.getId());
        dto.setNewUsername(targetUser.getUsername());
        dto.setEmail(targetUser.getEmail());
        dto.setCityId(targetUser.getCity() != null ? targetUser.getCity().getId() : null);

        formData.setChangeDetailsDto(dto);
        formData.setTargetUserId(targetUser.getId());
        formData.setTargetUsername(targetUser.getUsername());
        formData.setChangingOtherUser(!targetUser.getId().equals(currentUserEntity.getId()));
        formData.setCities(cityRepository.findAll());

        return formData;
    }

    @Override
    @Transactional
    public UserOperationResult deleteUser(Long userId, String performedByUsername) {
        Users performer = usersRepository.findByUsername(performedByUsername);
        Optional<Users> userOpt = usersRepository.findById(userId);
        Users userToDelete = userOpt.get();
        usersRepository.delete(userToDelete);
        return UserOperationResult.success(userId);
    }
}