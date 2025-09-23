package com.springten.wallet.service;

import com.springten.wallet.model.User;
import com.springten.wallet.model.Wallet;
import com.springten.wallet.repository.WalletRepository;
import com.springten.wallet.web3.Web3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
public class WalletService {
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private Web3Service web3Service;
    
    public Wallet createWalletForUser(User user) throws Exception {
        Wallet wallet = web3Service.generateEthereumWallet();
        wallet.setUser(user);
        
        // Generate recovery phrase
        String recoveryPhrase = web3Service.generateRecoveryPhrase();
        // In production, encrypt and store the recovery phrase securely
        
        user.setWalletAddress(wallet.getAddress());
        
        return walletRepository.save(wallet);
    }
    
    public BigInteger getBalance(String address) throws Exception {
        return web3Service.getBalance(address);
    }
    
    public BigDecimal weiToEth(BigInteger wei) {
        return new BigDecimal(wei).divide(new BigDecimal("1000000000000000000"));
    }
    
    public BigInteger ethToWei(BigDecimal eth) {
        return eth.multiply(new BigDecimal("1000000000000000000")).toBigInteger();
    }
    
    public String sendTransaction(String fromAddress, String toAddress, BigInteger amount, String privateKey) throws Exception {
        return web3Service.sendTransaction(fromAddress, toAddress, amount, privateKey);
    }
    
    public boolean isTransactionConfirmed(String transactionHash) throws Exception {
        return web3Service.isTransactionConfirmed(transactionHash);
    }
}
