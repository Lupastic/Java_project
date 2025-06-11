    package com.example.serving_web_content.Repo;

    import com.example.serving_web_content.models.Users;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;



    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
            return config.getAuthenticationManager();
        }
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth

                            .requestMatchers(HttpMethod.POST, "/register", "/guest", "/login", "/admin").permitAll()
                            .requestMatchers(HttpMethod.GET, "/register").permitAll()
                            .requestMatchers("/register", "/login", "/guest", "/", "/css/**", "/about", "/support", "/blog").permitAll()
                            .requestMatchers("/blog/add").hasRole("USER")
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/login")
                            .defaultSuccessUrl("/", true)
                            .permitAll()
                    )
                    .logout(logout -> logout.permitAll());

            return http.build();
        }


        @Bean
        public UserDetailsService userDetailsService(UsersRepository userRepository) {
            return username -> {
                Users user = userRepository.findByUsername(username);
                if (user == null) {
                    throw new UsernameNotFoundException("User not found");
                }

                return org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole())
                        .build();
            };
        }
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
