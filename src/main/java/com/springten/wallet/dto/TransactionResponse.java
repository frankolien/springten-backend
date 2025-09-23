package com.springten.wallet.dto;

import java.math.BigInteger;

public class TransactionResponse {
    
    private String transactionHash;
    private String status;
    private BigInteger gasUsed;
    private BigInteger gasPrice;
    private String fromAddress;
    private String toAddress;
    private BigInteger amount;
    private String error;
    
    // Constructors
    public TransactionResponse() {}
    
    public TransactionResponse(String transactionHash, String status) {
        this.transactionHash = transactionHash;
        this.status = status;
    }
    
    // Getters and Setters
    public String getTransactionHash() {
        return transactionHash;
    }
    
    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public BigInteger getGasUsed() {
        return gasUsed;
    }
    
    public void setGasUsed(BigInteger gasUsed) {
        this.gasUsed = gasUsed;
    }
    
    public BigInteger getGasPrice() {
        return gasPrice;
    }
    
    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }
    
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
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
}
