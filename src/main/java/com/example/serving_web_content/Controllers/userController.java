package com.example.serving_web_content.Controllers;
import com.example.serving_web_content.DTO.user.ChangeDetailsFormData;
import com.example.serving_web_content.DTO.user.CreateUserRequestDto;
import com.example.serving_web_content.Repo.CityRepository;
import com.example.serving_web_content.Repo.UsersRepository;
import com.example.serving_web_content.DTO.user.ChangeUserDetailsDto;
import com.example.serving_web_content.models.Users;
import com.example.serving_web_content.service.user.UserOperationResult;
import com.example.serving_web_content.service.user.usersService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
public class userController {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CityRepository cityRepository;
    private final usersService usersService;

    @Autowired
    public userController(UsersRepository usersRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          CityRepository cityRepository,
                          usersService usersService) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.cityRepository = cityRepository;
        this.usersService = usersService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new CreateUserRequestDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") CreateUserRequestDto userDto) {
        UserOperationResult result = usersService.createUser(userDto);
        return "redirect:/login";
    }

    @PostMapping("/guest")
    public String loginAsGuest(HttpServletRequest request) {
        UserDetails guestUser = User.withUsername("guest").password("").roles("GUEST").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(guestUser, null, guestUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        return "redirect:/";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/admin")
    public String loginAsAdmin(HttpServletRequest request) {
        UserDetails adminUser = User.withUsername("admin").password("").roles("ADMIN").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(adminUser, null, adminUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        return "redirect:/";
    }


    @GetMapping("/change")
    @PreAuthorize("isAuthenticated()")
    public String showChangeDetailsForm(Model model,
                                        @RequestParam(value = "userId", required = false) Long targetUserIdParam,
                                        @AuthenticationPrincipal UserDetails currentUserDetails) {
        ChangeDetailsFormData formData = usersService.getChangeUserDetailsDto(targetUserIdParam, currentUserDetails);
        model.addAttribute("changeDetailsDto", formData.getChangeDetailsDto());
        model.addAttribute("targetUserId", formData.getTargetUserId());
        model.addAttribute("targetUsername", formData.getTargetUsername());
        model.addAttribute("changingOtherUser", formData.isChangingOtherUser());
        model.addAttribute("isCurrentUserAdmin", formData.isCurrentUserAdmin());
        model.addAttribute("cities", formData.getCities());
        return "changeDetails";
    }
    @PostMapping("/change")
    public String updateUserDetails(@ModelAttribute("changeDetailsDto") ChangeUserDetailsDto dto,
                                    @AuthenticationPrincipal UserDetails currentUserDetails,
                                    RedirectAttributes redirectAttributes) {

        String currentUsername = currentUserDetails.getUsername();
        UserOperationResult result = usersService.updateUserDetails(dto, currentUsername);
        if (result.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", "Данные успешно обновлены.");
            return "redirect:/login";
        } else {
            Users currentUserEntity = usersRepository.findByUsername(currentUsername);
            Long currentUserId = (currentUserEntity != null) ? currentUserEntity.getId() : null;

            if (currentUserId != null) {
                return "redirect:/change?userId=" + currentUserId;
            } else {
                return "redirect:/change";
            }
        }
    }
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminUsers(@RequestParam(name = "page",defaultValue = "0") int pageNumber,
                             @RequestParam(name = "size", defaultValue = "5") int pageSize,
                             @RequestParam(name = "usernameSearchTerm", required = false) String usernameSearchTerm,
                             Model model) {
        Page<Users> usersPage = usersService.findUsers(PageRequest.of(pageNumber, pageSize), usernameSearchTerm);
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("usernameSearchTerm", usernameSearchTerm);
        return "allUsers";
    }
    @PostMapping("/admin/users/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@RequestParam("id") Long userId,
                             @AuthenticationPrincipal UserDetails currentUserDetails) {
        String performedByUsername = currentUserDetails.getUsername();
        usersService.deleteUser(userId, performedByUsername);
        return "redirect:/admin/users";
    }
    @GetMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String changeUser(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Users> userOptional = usersService.findById(id);
        if (!userOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пользователь с ID " + id + " не найден.");
            return "redirect:/admin/users";
        }
        Users user = userOptional.get();
        ChangeUserDetailsDto dto = new ChangeUserDetailsDto();
        dto.setTargetUserId(user.getId());
        dto.setNewUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCityId(user.getCity() != null ? user.getCity().getId() : null);
        model.addAttribute("changingOtherUser", true);
        model.addAttribute("changeDetailsDto", dto);
        model.addAttribute("targetUserId", user.getId());
        model.addAttribute("targetUsername", user.getUsername());
        model.addAttribute("cities", cityRepository.findAll());
        return "changeUser";
    }
    @PostMapping("/admin/users/{id}")
    public String updateUserAdminPost(@PathVariable("id") Long userId,
                                      @ModelAttribute("changeUserDetailsDto") ChangeUserDetailsDto dto,
                                      RedirectAttributes redirectAttributes,
                                      Principal principal) {
        String adminUsername = principal.getName();
        UserOperationResult result = usersService.updateUserAdmin(dto, userId, adminUsername);
        if (result.isSuccess()) {
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно обновлен");
            return "redirect:/admin/users";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Не удалось обновить пользователя: " + result.getErrorMessage());
            return "redirect:/admin/users";
        }
    }
    @GetMapping("/admin/users/{id}/edit")
    public String showEditUserForm(@PathVariable("id") Long userId, Model model) {
        Optional<Users> userOptional = usersRepository.findById(userId);
        Users user = userOptional.get();
        model.addAttribute("user", user);
        return "changeUser";
    }
}