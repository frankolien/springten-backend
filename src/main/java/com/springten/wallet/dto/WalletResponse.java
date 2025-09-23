package com.springten.wallet.dto;

public class WalletResponse {
    
    private String address;
    private String username;
    private String fullName;
    private Boolean biometricEnabled;
    private String error;
    
    // Constructors
    public WalletResponse() {}
    
    public WalletResponse(String address, String username, String fullName, Boolean biometricEnabled) {
        this.address = address;
        this.username = username;
        this.fullName = fullName;
        this.biometricEnabled = biometricEnabled;
    }
    
    // Getters and Setters
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public Boolean getBiometricEnabled() {
        return biometricEnabled;
    }
    
    public void setBiometricEnabled(Boolean biometricEnabled) {
        this.biometricEnabled = biometricEnabled;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}
