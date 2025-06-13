package com.example.serving_web_content.Controllers;

import com.example.serving_web_content.Repo.UsersRepository;
import com.example.serving_web_content.models.Users;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.Collections;

@Controller
public class MainController {

    private final UsersRepository usersRepository;

    public MainController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "главная страница");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "О нас");
        return "about";
    }

    @GetMapping("/support")
    public String support(Model model) {
        model.addAttribute("title", "Поддержка");
        return "support";
    }
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String userProfile(Model model, Principal principal) {
        String username = principal.getName();
        Users currentUser = usersRepository.findByUsername(username);

        if (currentUser == null) {
            // Симулированный пользователь (например, admin)
            Object principalObject = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principalObject instanceof UserDetails userDetails) {
                model.addAttribute("username", userDetails.getUsername());
                model.addAttribute("roles", userDetails.getAuthorities());
                model.addAttribute("city", "Unknown (simulated)");
            } else {
                model.addAttribute("username", "Unknown");
                model.addAttribute("roles", Collections.emptyList());
                model.addAttribute("city", "Unknown");
            }
            model.addAttribute("title", "Simulated Profile");
            return "profile_simulated";
        }

        // Реальный пользователь
        model.addAttribute("user", currentUser);
        model.addAttribute("city", currentUser.getCity() != null ? currentUser.getCity().getName() : "Not specified");
        model.addAttribute("title", "Профиль пользователя");
        return "profile";
    }



    private Users getCurrentUser(Principal principal) {
        return usersRepository.findByUsername(principal.getName());
    }
}
