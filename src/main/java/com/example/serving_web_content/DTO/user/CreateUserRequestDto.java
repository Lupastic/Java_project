package com.example.serving_web_content.DTO.user;

public class CreateUserRequestDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String ROLE;
    public String getROLE() {
        return ROLE;
    }
    public void setROLE(String ROLE) {
        this.ROLE = ROLE;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
