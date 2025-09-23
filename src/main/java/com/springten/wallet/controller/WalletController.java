package com.springten.wallet.controller;

import com.springten.wallet.dto.WalletResponse;
import com.springten.wallet.model.User;
import com.springten.wallet.service.UserService;
import com.springten.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*")
public class WalletController {
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getWalletBalance(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            BigInteger balance = walletService.getBalance(user.getWalletAddress());
            
            Map<String, Object> response = new HashMap<>();
            response.put("address", user.getWalletAddress());
            response.put("balance", balance.toString());
            response.put("balanceEth", walletService.weiToEth(balance));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to get balance: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/info")
    public ResponseEntity<WalletResponse> getWalletInfo(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            WalletResponse response = new WalletResponse();
            response.setAddress(user.getWalletAddress());
            response.setUsername(user.getUsername());
            response.setFullName(user.getFullName());
            response.setBiometricEnabled(user.getBiometricEnabled());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            WalletResponse response = new WalletResponse();
            response.setError("Failed to get wallet info: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createWallet(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username);
            
            // Generate new wallet if user doesn't have one
            if (user.getWalletAddress() == null) {
                walletService.createWalletForUser(user);
                userService.updateUser(user);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Wallet created successfully");
            response.put("address", user.getWalletAddress());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to create wallet: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
