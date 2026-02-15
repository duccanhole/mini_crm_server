package com.mini_crm.main.dto.auth;

public class LoginResponse {
    private String token;
    private String email;
    private String role;
    private String name;
    private String phone;
    private Long id;

    public LoginResponse() {
    }

    public LoginResponse(String token, String email, String role, String name, Long id, String phone) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.name = name;
        this.id = id;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }   

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
