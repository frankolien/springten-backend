package com.springten.wallet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Springten Wallet API");
        response.put("version", "1.0.0");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "Springten Crypto Wallet");
        response.put("description", "Web3 Crypto Wallet Backend API");
        response.put("features", new String[]{
            "User Authentication & Security",
            "Wallet Creation & Management", 
            "Token Swapping",
            "Buy/Sell Operations",
            "Staking",
            "Transaction History",
            "Real-time Price Data"
        });
        
        return ResponseEntity.ok(response);
    }
}
