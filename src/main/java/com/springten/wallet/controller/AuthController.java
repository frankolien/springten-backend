package com.springten.wallet.controller;

import com.springten.wallet.dto.LoginRequest;
import com.springten.wallet.dto.LoginResponse;
import com.springten.wallet.dto.RegisterRequest;
import com.springten.wallet.dto.RegisterResponse;
import com.springten.wallet.model.User;
import com.springten.wallet.security.JwtUtil;
import com.springten.wallet.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            User user = userService.registerUser(request);
            String token = jwtUtil.generateToken(userService.loadUserByUsername(user.getUsername()));
            
            RegisterResponse response = new RegisterResponse();
            response.setMessage("User registered successfully");
            response.setToken(token);
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            RegisterResponse response = new RegisterResponse();
            response.setMessage("Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            
            LoginResponse response = new LoginResponse();
            response.setMessage("Login successful");
            response.setToken(token);
            response.setUsername(userDetails.getUsername());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LoginResponse response = new LoginResponse();
            response.setMessage("Login failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String username = jwtUtil.extractUsername(token);
                UserDetails userDetails = userService.loadUserByUsername(username);
                String newToken = jwtUtil.generateToken(userDetails);
                
                LoginResponse response = new LoginResponse();
                response.setMessage("Token refreshed successfully");
                response.setToken(newToken);
                response.setUsername(username);
                
                return ResponseEntity.ok(response);
            } else {
                LoginResponse response = new LoginResponse();
                response.setMessage("Invalid token");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            LoginResponse response = new LoginResponse();
            response.setMessage("Token refresh failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/create-wallet")
    public ResponseEntity<Map<String, Object>> createWalletOnly() {
        try {
            // Create a temporary user for wallet creation
            User user = userService.createWalletOnlyUser();
            String token = jwtUtil.generateToken(userService.loadUserByUsername(user.getUsername()));
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Wallet created successfully");
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("address", user.getWalletAddress());
            response.put("recoveryPhrase", user.getRecoveryPhrase());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Wallet creation failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/import-wallet")
    public ResponseEntity<Map<String, Object>> importWallet(@RequestBody Map<String, String> request) {
        try {
            String recoveryPhrase = request.get("recoveryPhrase");
            if (recoveryPhrase == null || recoveryPhrase.trim().isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Recovery phrase is required");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Import wallet using recovery phrase
            User user = userService.importWalletFromRecoveryPhrase(recoveryPhrase);
            String token = jwtUtil.generateToken(userService.loadUserByUsername(user.getUsername()));
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Wallet imported successfully");
            response.put("token", token);
            response.put("userId", user.getId());
            response.put("address", user.getWalletAddress());
            response.put("recoveryPhrase", user.getRecoveryPhrase());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Wallet import failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
