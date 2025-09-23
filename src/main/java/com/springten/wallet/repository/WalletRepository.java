package com.springten.wallet.repository;

import com.springten.wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    Optional<Wallet> findByAddress(String address);
    
    List<Wallet> findByUserId(Long userId);
    
    List<Wallet> findByBlockchainNetwork(Wallet.BlockchainNetwork blockchainNetwork);
    
    List<Wallet> findByIsActive(Boolean isActive);
}
