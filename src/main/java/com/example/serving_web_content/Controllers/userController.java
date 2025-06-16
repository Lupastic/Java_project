package com.example.serving_web_content.Controllers;

import com.example.serving_web_content.Repo.CityRepository;
import com.example.serving_web_content.Repo.UsersRepository;
import com.example.serving_web_content.models.ChangeUserDetailsDto;
import com.example.serving_web_content.models.City;
import com.example.serving_web_content.models.Users;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class userController {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CityRepository cityRepository;

    @Autowired
    public userController(UsersRepository usersRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          CityRepository cityRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.cityRepository = cityRepository;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new Users());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") Users user) {
        System.out.println(">>> REGISTER START: " + user.getUsername());
        String rawPassword = user.getPassword();
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("USER");
        System.out.println(">>> ENCODED PASSWORD: " + user.getPassword());
        usersRepository.save(user);
        System.out.println(">>> USER SAVED");
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), rawPassword)
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/login";
    }

    @PostMapping("/guest")
    public String loginAsGuest(HttpServletRequest request) {
        UserDetails guestUser = User
                .withUsername("guest")
                .password("")
                .roles("GUEST")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                guestUser, null, guestUser.getAuthorities()
        );
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
        UserDetails guestUser = User
                .withUsername("admin")
                .password("")
                .roles("ADMIN")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                guestUser, null, guestUser.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
        return "redirect:/";
    }

    @GetMapping("/change")
    @PreAuthorize("isAuthenticated()")
    public String showChangeDetailsForm(Model model,
                                        @RequestParam(value = "userId", required = false) Long userIdParam) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Users currentUser = usersRepository.findByUsername(username);

        Users targetUser = (userIdParam != null && userIdParam.equals(currentUser.getId()))
                ? currentUser
                : currentUser;
        ChangeUserDetailsDto dto = new ChangeUserDetailsDto();
        dto.setNewUsername(targetUser.getUsername());
        dto.setEmail(targetUser.getEmail());

        model.addAttribute("changeDetailsDto", dto);
        model.addAttribute("isCurrentUserAdmin", false);
        model.addAttribute("changingOtherUser", false);
        model.addAttribute("cities", cityRepository.findAll());
        return "changeDetails";
    }


    @PostMapping("/change")
    public String updateUserDetails(@ModelAttribute("changeDetailsDto") ChangeUserDetailsDto dto,
                                    Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        Users currentUser = usersRepository.findByUsername(currentPrincipalName);
        Users userToUpdate;
        if (dto.getTargetUserId() != null) {
            Optional<Users> optionalUser = usersRepository.findById(dto.getTargetUserId());
            if (optionalUser.isPresent()) {
                userToUpdate = optionalUser.get();
            } else {
                return "changeDetails";
            }
        } else {
            userToUpdate = currentUser;
        }
        userToUpdate.setUsername(dto.getNewUsername());
        userToUpdate.setEmail(dto.getEmail());

        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
            userToUpdate.setPassword(encodedPassword);
        }
        City selectedCity = null;
        if (dto.getCityId() != null) {
            Optional<City> optionalCity = cityRepository.findById(dto.getCityId());
            selectedCity = optionalCity.get();
        }
        userToUpdate.setCity(selectedCity);
        usersRepository.save(userToUpdate);
        model.addAttribute("cities", cityRepository.findAll());
        model.addAttribute("successMessage", "Данные успешно обновлены.");
        return "redirect:/profile";
    }

    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        Iterable<Users> users = usersRepository.findAll();
        model.addAttribute("users", users);
        return "allUsers";
    }

    @PostMapping("/admin/users/delete")
    public String deleteUser(@RequestParam("id") Long id,
                             RedirectAttributes redirectAttributes) {
        Optional<Users> userOptional = usersRepository.findById(id);
        Users userToDelete = userOptional.get();
        usersRepository.delete(userToDelete);
        redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно удалён.");
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/{id}")
    public String changeUser(@PathVariable("id") Long id, Model model) {
        Optional<Users> userOptional = usersRepository.findById(id);
        if (!userOptional.isPresent()) {
            model.addAttribute("errorMessage", "Пользователь с ID " + id + " не найден.");
            return "admin/allUsers";
        }
        Users user = userOptional.get();
        ChangeUserDetailsDto dto = new ChangeUserDetailsDto();
        dto.setTargetUserId(user.getId());
        dto.setNewUsername(user.getUsername());
        dto.setEmail(user.getEmail());

        model.addAttribute("changingOtherUser", true);
        model.addAttribute("changeDetailsDto", dto);
        model.addAttribute("cities", cityRepository.findAll());
        return "changeUser";
    }

    @PostMapping("/changeUser")
    public String changeUserPost(@ModelAttribute("changeDetailsDto") ChangeUserDetailsDto dto,
                                 Model model) {
        Optional<Users> optionalUser = usersRepository.findById(dto.getTargetUserId());
        Users userToUpdate = optionalUser.get();
        model.addAttribute("changingOtherUser", true);
        userToUpdate.setUsername(dto.getNewUsername());

        if (dto.getNewPassword() != null && !dto.getNewPassword().isBlank()) {
            if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
                model.addAttribute("errorMessage", "Пароли не совпадают.");
                return "changeDetails";
            }
            String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
            userToUpdate.setPassword(encodedPassword);
        }
        City selectedCity = null;
        if (dto.getCityId() != null) {
            Optional<City> optionalCity = cityRepository.findById(dto.getCityId());
            selectedCity = optionalCity.get();
        }
        userToUpdate.setCity(selectedCity);
        usersRepository.save(userToUpdate);
        model.addAttribute("cities", cityRepository.findAll());
        model.addAttribute("successMessage", "Данные пользователя успешно обновлены.");
        return "redirect:/admin/users";
    }


}