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
@Table(name = "tokens")
@EntityListeners(AuditingEntityListener.class)
public class Token {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(name = "symbol", unique = true)
    private String symbol;
    
    @NotBlank
    @Column(name = "name")
    private String name;
    
    @Column(name = "contract_address")
    private String contractAddress;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "blockchain_network")
    private Wallet.BlockchainNetwork blockchainNetwork;
    
    @Column(name = "decimals")
    private Integer decimals = 18;
    
    @Column(name = "current_price_usd", precision = 36, scale = 18)
    private BigDecimal currentPriceUsd = BigDecimal.ZERO;
    
    @Column(name = "market_cap_usd", precision = 36, scale = 18)
    private BigDecimal marketCapUsd = BigDecimal.ZERO;
    
    @Column(name = "volume_24h_usd", precision = 36, scale = 18)
    private BigDecimal volume24hUsd = BigDecimal.ZERO;
    
    @Column(name = "price_change_24h_percent", precision = 10, scale = 4)
    private BigDecimal priceChange24hPercent = BigDecimal.ZERO;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "logo_url")
    private String logoUrl;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public Token() {}
    
    public Token(String symbol, String name, String contractAddress, 
                 Wallet.BlockchainNetwork blockchainNetwork, Integer decimals) {
        this.symbol = symbol;
        this.name = name;
        this.contractAddress = contractAddress;
        this.blockchainNetwork = blockchainNetwork;
        this.decimals = decimals;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getContractAddress() {
        return contractAddress;
    }
    
    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }
    
    public Wallet.BlockchainNetwork getBlockchainNetwork() {
        return blockchainNetwork;
    }
    
    public void setBlockchainNetwork(Wallet.BlockchainNetwork blockchainNetwork) {
        this.blockchainNetwork = blockchainNetwork;
    }
    
    public Integer getDecimals() {
        return decimals;
    }
    
    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }
    
    public BigDecimal getCurrentPriceUsd() {
        return currentPriceUsd;
    }
    
    public void setCurrentPriceUsd(BigDecimal currentPriceUsd) {
        this.currentPriceUsd = currentPriceUsd;
    }
    
    public BigDecimal getMarketCapUsd() {
        return marketCapUsd;
    }
    
    public void setMarketCapUsd(BigDecimal marketCapUsd) {
        this.marketCapUsd = marketCapUsd;
    }
    
    public BigDecimal getVolume24hUsd() {
        return volume24hUsd;
    }
    
    public void setVolume24hUsd(BigDecimal volume24hUsd) {
        this.volume24hUsd = volume24hUsd;
    }
    
    public BigDecimal getPriceChange24hPercent() {
        return priceChange24hPercent;
    }
    
    public void setPriceChange24hPercent(BigDecimal priceChange24hPercent) {
        this.priceChange24hPercent = priceChange24hPercent;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getLogoUrl() {
        return logoUrl;
    }
    
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
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
}
