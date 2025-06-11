package com.example.serving_web_content.Contrellers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.serving_web_content.Repo.UsersRepository;
import com.example.serving_web_content.models.Users;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class userController {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public userController(UsersRepository usersRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
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
        return "redirect:/";
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
}
