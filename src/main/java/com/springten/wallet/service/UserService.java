package com.springten.wallet.service;

import com.springten.wallet.dto.RegisterRequest;
import com.springten.wallet.model.User;
import com.springten.wallet.repository.UserRepository;
import com.springten.wallet.web3.Web3Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Web3Service web3Service;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, Web3Service web3Service) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.web3Service = web3Service;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(new ArrayList<>())
                .build();
    }
    
    public User registerUser(RegisterRequest request) throws Exception {
        // Check if user already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setBiometricEnabled(request.getBiometricEnabled());
        
        // Generate wallet for the user
        try {
            com.springten.wallet.model.Wallet wallet = web3Service.generateEthereumWallet();
            user.setWalletAddress(wallet.getAddress());
            
            // Generate recovery phrase
            String recoveryPhrase = web3Service.generateRecoveryPhrase();
            user.setRecoveryPhraseHash(passwordEncoder.encode(recoveryPhrase));
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate wallet: " + e.getMessage());
        }
        
        return userRepository.save(user);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    public User createWalletOnlyUser() throws Exception {
        // Generate unique username for wallet-only user
        String username = "wallet_" + System.currentTimeMillis();
        String email = "wallet_" + System.currentTimeMillis() + "@springten.app";
        
        // Create new user with minimal data
        User user = new User();
        user.setUsername(username);
        user.setEmail(email); // Use unique email for each wallet-only user
        user.setPassword(passwordEncoder.encode("wallet")); // Simple password for wallet-only users
        user.setFullName("Wallet User");
        user.setBiometricEnabled(false);
        
        // Generate wallet and recovery phrase together (they are now properly linked)
        try {
            Web3Service.WalletWithRecoveryPhrase walletData = web3Service.generateWalletWithRecoveryPhrase();
            user.setWalletAddress(walletData.getWallet().getAddress());
            user.setRecoveryPhrase(walletData.getRecoveryPhrase()); // Store actual phrase for wallet-only users
            // Don't hash the recovery phrase as it might be too long for BCrypt
            user.setRecoveryPhraseHash(""); // Empty hash for wallet-only users
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate wallet: " + e.getMessage());
        }
        
        return userRepository.save(user);
    }
    
    public User importWalletFromRecoveryPhrase(String recoveryPhrase) throws Exception {
        // Generate unique username for imported wallet user
        String username = "imported_" + System.currentTimeMillis();
        String email = "imported_" + System.currentTimeMillis() + "@springten.app";
        
        // Create new user with minimal data
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("imported")); // Simple password for imported users
        user.setFullName("Imported Wallet User");
        user.setBiometricEnabled(false);
        
        // Import wallet from recovery phrase
        try {
            com.springten.wallet.model.Wallet wallet = web3Service.importWalletFromRecoveryPhrase(recoveryPhrase);
            user.setWalletAddress(wallet.getAddress());
            
            // Store the recovery phrase
            user.setRecoveryPhrase(recoveryPhrase);
            user.setRecoveryPhraseHash(""); // Empty hash for imported users
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to import wallet: " + e.getMessage());
        }
        
        return userRepository.save(user);
    }
}
