package com.springten.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigInteger;

public class TransactionRequest {
    
    @NotBlank(message = "From address is required")
    private String fromAddress;
    
    @NotBlank(message = "To address is required")
    private String toAddress;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigInteger amount;
    
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private String privateKey;
    
    // Constructors
    public TransactionRequest() {}
    
    public TransactionRequest(String fromAddress, String toAddress, BigInteger amount) {
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
    }
    
    // Getters and Setters
    public String getFromAddress() {
        return fromAddress;
    }
    
    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
    
    public String getToAddress() {
        return toAddress;
    }
    
    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }
    
    public BigInteger getAmount() {
        return amount;
    }
    
    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }
    
    public BigInteger getGasPrice() {
        return gasPrice;
    }
    
    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }
    
    public BigInteger getGasLimit() {
        return gasLimit;
    }
    
    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }
    
    public String getPrivateKey() {
        return privateKey;
    }
    
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
