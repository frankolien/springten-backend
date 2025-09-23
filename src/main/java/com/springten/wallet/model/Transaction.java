package com.springten.wallet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "transaction_hash", unique = true)
    private String transactionHash;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status;
    
    @Column(name = "from_address")
    private String fromAddress;
    
    @Column(name = "to_address")
    private String toAddress;
    
    @Column(name = "amount", precision = 36, scale = 18)
    private BigDecimal amount;
    
    @Column(name = "amount_usd", precision = 36, scale = 18)
    private BigDecimal amountUsd;
    
    @Column(name = "gas_price", precision = 36, scale = 18)
    private BigDecimal gasPrice;
    
    @Column(name = "gas_limit")
    private Long gasLimit;
    
    @Column(name = "gas_used")
    private Long gasUsed;
    
    @Column(name = "network_fee", precision = 36, scale = 18)
    private BigDecimal networkFee;
    
    @Column(name = "provider_fee", precision = 36, scale = 18)
    private BigDecimal providerFee;
    
    @Column(name = "total_fee", precision = 36, scale = 18)
    private BigDecimal totalFee;
    
    @Column(name = "exchange_rate", precision = 36, scale = 18)
    private BigDecimal exchangeRate;
    
    @Column(name = "slippage_percent", precision = 10, scale = 4)
    private BigDecimal slippagePercent;
    
    @Column(name = "block_number")
    private Long blockNumber;
    
    @Column(name = "confirmation_count")
    private Integer confirmationCount = 0;
    
    @Column(name = "memo")
    private String memo;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id")
    private Token token;
    
    // Constructors
    public Transaction() {}
    
    public Transaction(String transactionHash, TransactionType transactionType, 
                      TransactionStatus status, User user, Wallet wallet) {
        this.transactionHash = transactionHash;
        this.transactionType = transactionType;
        this.status = status;
        this.user = user;
        this.wallet = wallet;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTransactionHash() {
        return transactionHash;
    }
    
    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransactionStatus status) {
        this.status = status;
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
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public BigDecimal getAmountUsd() {
        return amountUsd;
    }
    
    public void setAmountUsd(BigDecimal amountUsd) {
        this.amountUsd = amountUsd;
    }
    
    public BigDecimal getGasPrice() {
        return gasPrice;
    }
    
    public void setGasPrice(BigDecimal gasPrice) {
        this.gasPrice = gasPrice;
    }
    
    public Long getGasLimit() {
        return gasLimit;
    }
    
    public void setGasLimit(Long gasLimit) {
        this.gasLimit = gasLimit;
    }
    
    public Long getGasUsed() {
        return gasUsed;
    }
    
    public void setGasUsed(Long gasUsed) {
        this.gasUsed = gasUsed;
    }
    
    public BigDecimal getNetworkFee() {
        return networkFee;
    }
    
    public void setNetworkFee(BigDecimal networkFee) {
        this.networkFee = networkFee;
    }
    
    public BigDecimal getProviderFee() {
        return providerFee;
    }
    
    public void setProviderFee(BigDecimal providerFee) {
        this.providerFee = providerFee;
    }
    
    public BigDecimal getTotalFee() {
        return totalFee;
    }
    
    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }
    
    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }
    
    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
    
    public BigDecimal getSlippagePercent() {
        return slippagePercent;
    }
    
    public void setSlippagePercent(BigDecimal slippagePercent) {
        this.slippagePercent = slippagePercent;
    }
    
    public Long getBlockNumber() {
        return blockNumber;
    }
    
    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }
    
    public Integer getConfirmationCount() {
        return confirmationCount;
    }
    
    public void setConfirmationCount(Integer confirmationCount) {
        this.confirmationCount = confirmationCount;
    }
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }
    
    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Wallet getWallet() {
        return wallet;
    }
    
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
    
    public Token getToken() {
        return token;
    }
    
    public void setToken(Token token) {
        this.token = token;
    }
    
    public enum TransactionType {
        SEND, RECEIVE, SWAP, BUY, SELL, STAKE, UNSTAKE, DEPOSIT, WITHDRAW
    }
    
    public enum TransactionStatus {
        PENDING, CONFIRMED, FAILED, CANCELLED, EXPIRED
    }
}
