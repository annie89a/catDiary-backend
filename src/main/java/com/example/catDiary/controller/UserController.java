package com.example.catDiary.controller;

import com.example.catDiary.model.User;
import com.example.catDiary.security.JwtUtil;
import com.example.catDiary.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/apis/user/register")
    public ApiResponse<UserResponse> registerUser(@RequestBody UserRegistrationRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ApiResponse.error("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return ApiResponse.error("Password must be at least 6 characters");
        }
        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            return ApiResponse.error("Valid email is required");
        }

        try {
            User user = userService.registerUser(request.getUsername(), request.getPassword(), request.getEmail());
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            UserResponse userResponse = createUserResponse(user, token);
            return ApiResponse.success(userResponse, "User registered successfully");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/apis/user/login")
    public ApiResponse<UserResponse> loginUser(@RequestBody UserLoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ApiResponse.error("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ApiResponse.error("Password is required");
        }

        try {
            User user = userService.findByUsername(request.getUsername());
            if (!user.isEnabled()) {
                return ApiResponse.error("Account is disabled");
            }
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ApiResponse.error("Invalid username or password");
            }

            String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
            UserResponse userResponse = createUserResponse(user, token);
            return ApiResponse.success(userResponse, "Login successful");
        } catch (RuntimeException e) {
            return ApiResponse.error("Invalid username or password");
        }
    }

    @GetMapping("/apis/user/profile")
    public ApiResponse<UserResponse> getUserProfile() {
        try {
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getName() == null) {
                return ApiResponse.error("Authentication required");
            }
            User user = userService.findByUsername(auth.getName());
            UserResponse userResponse = createUserResponse(user, null);
            return ApiResponse.success(userResponse, "User profile retrieved successfully");
        } catch (RuntimeException e) {
            return ApiResponse.error("User not found");
        }
    }

    private UserResponse createUserResponse(User user, String token) {
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setEmail(user.getEmail());
        resp.setRole(user.getRole());
        resp.setEnabled(user.isEnabled());
        resp.setToken(token);
        return resp;
    }

    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String role;
        private boolean enabled;
        private String token;

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

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class UserRegistrationRequest {
        private String username;
        private String password;
        private String email;

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

    public static class UserLoginRequest {
        private String username;
        private String password;

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
    }
}
