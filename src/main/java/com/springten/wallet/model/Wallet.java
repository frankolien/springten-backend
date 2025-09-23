package com.springten.wallet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallets")
@EntityListeners(AuditingEntityListener.class)
public class Wallet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "address", unique = true)
    private String address;
    
    @NotBlank
    @Column(name = "private_key_hash")
    private String privateKeyHash;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "blockchain_network")
    private BlockchainNetwork blockchainNetwork;
    
    @Column(name = "balance", precision = 36, scale = 18)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TokenBalance> tokenBalances = new ArrayList<>();
    
    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();
    
    // Constructors
    public Wallet() {}
    
    public Wallet(String address, String privateKeyHash, BlockchainNetwork blockchainNetwork, User user) {
        this.address = address;
        this.privateKeyHash = privateKeyHash;
        this.blockchainNetwork = blockchainNetwork;
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPrivateKeyHash() {
        return privateKeyHash;
    }
    
    public void setPrivateKeyHash(String privateKeyHash) {
        this.privateKeyHash = privateKeyHash;
    }
    
    public BlockchainNetwork getBlockchainNetwork() {
        return blockchainNetwork;
    }
    
    public void setBlockchainNetwork(BlockchainNetwork blockchainNetwork) {
        this.blockchainNetwork = blockchainNetwork;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public List<TokenBalance> getTokenBalances() {
        return tokenBalances;
    }
    
    public void setTokenBalances(List<TokenBalance> tokenBalances) {
        this.tokenBalances = tokenBalances;
    }
    
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    
    public enum BlockchainNetwork {
        ETHEREUM_MAINNET,
        ETHEREUM_GOERLI,
        BITCOIN_MAINNET,
        BITCOIN_TESTNET,
        POLYGON_MAINNET,
        BSC_MAINNET,
        SOLANA_MAINNET
    }
}
