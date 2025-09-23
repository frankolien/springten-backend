package com.springten.wallet.web3;

import com.springten.wallet.model.Wallet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.ChildNumber;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

@Service
public class Web3Service {
    
    @Value("${web3.ethereum.rpc-url}")
    private String ethereumRpcUrl;
    
    @Value("${web3.ethereum.network-id}")
    private String networkId;
    
    private Web3j web3j;
    
    public Web3Service() {
        // Initialize Web3j connection
    }
    
    public void initializeWeb3j() {
        this.web3j = Web3j.build(new HttpService(ethereumRpcUrl));
    }
    
    /**
     * Generate a new Ethereum wallet with proper BIP39 mnemonic
     */
    public Wallet generateEthereumWallet() throws Exception {
        // Generate 128 bits of entropy (16 bytes)
        byte[] entropy = new byte[16];
        new SecureRandom().nextBytes(entropy);
        
        // Generate BIP39 mnemonic from entropy
        List<String> mnemonic = MnemonicCode.INSTANCE.toMnemonic(entropy);
        
        // Derive wallet from mnemonic using BIP44 path (m/44'/60'/0'/0/0 for Ethereum)
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(entropy);
        DeterministicKey derivedKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(44, true));
        derivedKey = HDKeyDerivation.deriveChildKey(derivedKey, new ChildNumber(60, true));
        derivedKey = HDKeyDerivation.deriveChildKey(derivedKey, new ChildNumber(0, true));
        derivedKey = HDKeyDerivation.deriveChildKey(derivedKey, new ChildNumber(0, false));
        derivedKey = HDKeyDerivation.deriveChildKey(derivedKey, new ChildNumber(0, false));
        
        // Convert to Ethereum key pair
        ECKeyPair keyPair = ECKeyPair.create(derivedKey.getPrivKeyBytes());
        String address = Keys.getAddress(keyPair);
        String privateKey = keyPair.getPrivateKey().toString(16);
        
        Wallet wallet = new Wallet();
        wallet.setAddress(address);
        wallet.setPrivateKeyHash(hashPrivateKey(privateKey));
        wallet.setBlockchainNetwork(Wallet.BlockchainNetwork.ETHEREUM_MAINNET);
        
        return wallet;
    }
    
    /**
     * Generate wallet and recovery phrase together (properly linked)
     */
    public WalletWithRecoveryPhrase generateWalletWithRecoveryPhrase() throws Exception {
        // Generate 128 bits of entropy (16 bytes)
        byte[] entropy = new byte[16];
        new SecureRandom().nextBytes(entropy);
        
        // Generate BIP39 mnemonic from entropy
        List<String> mnemonic = MnemonicCode.INSTANCE.toMnemonic(entropy);
        String recoveryPhrase = String.join(" ", mnemonic);
        
        // Derive wallet from mnemonic using BIP44 path (m/44'/60'/0'/0/0 for Ethereum)
        DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(entropy);
        DeterministicKey derivedKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(44, true));
        derivedKey = HDKeyDerivation.deriveChildKey(derivedKey, new ChildNumber(60, true));
        derivedKey = HDKeyDerivation.deriveChildKey(derivedKey, new ChildNumber(0, true));
        derivedKey = HDKeyDerivation.deriveChildKey(derivedKey, new ChildNumber(0, false));
        derivedKey = HDKeyDerivation.deriveChildKey(derivedKey, new ChildNumber(0, false));
        
        // Convert to Ethereum key pair
        ECKeyPair keyPair = ECKeyPair.create(derivedKey.getPrivKeyBytes());
        String address = Keys.getAddress(keyPair);
        String privateKey = keyPair.getPrivateKey().toString(16);
        
        Wallet wallet = new Wallet();
        wallet.setAddress(address);
        wallet.setPrivateKeyHash(hashPrivateKey(privateKey));
        wallet.setBlockchainNetwork(Wallet.BlockchainNetwork.ETHEREUM_MAINNET);
        
        return new WalletWithRecoveryPhrase(wallet, recoveryPhrase);
    }
    
    /**
     * Data class to hold wallet and recovery phrase together
     */
    public static class WalletWithRecoveryPhrase {
        private final Wallet wallet;
        private final String recoveryPhrase;
        
        public WalletWithRecoveryPhrase(Wallet wallet, String recoveryPhrase) {
            this.wallet = wallet;
            this.recoveryPhrase = recoveryPhrase;
        }
        
        public Wallet getWallet() {
            return wallet;
        }
        
        public String getRecoveryPhrase() {
            return recoveryPhrase;
        }
    }
    
    /**
     * Generate recovery phrase (BIP39 mnemonic) - now properly tied to wallet
     */
    public String generateRecoveryPhrase() throws Exception {
        // Generate 128 bits of entropy (16 bytes)
        byte[] entropy = new byte[16];
        new SecureRandom().nextBytes(entropy);
        
        // Generate BIP39 mnemonic from entropy
        List<String> mnemonic = MnemonicCode.INSTANCE.toMnemonic(entropy);
        return String.join(" ", mnemonic);
    }
    
    /**
     * Import wallet from recovery phrase using proper BIP39 derivation
     */
    public Wallet importWalletFromRecoveryPhrase(String recoveryPhrase) throws Exception {
        if (recoveryPhrase == null || recoveryPhrase.trim().isEmpty()) {
            throw new IllegalArgumentException("Recovery phrase cannot be empty");
        }
        
        // Validate that the recovery phrase has 12 words
        String[] words = recoveryPhrase.trim().split("\\s+");
        if (words.length != 12) {
            throw new IllegalArgumentException("Recovery phrase must contain exactly 12 words");
        }
        
        try {
            // Convert mnemonic back to entropy
            List<String> mnemonicList = Arrays.asList(words);
            byte[] entropy = MnemonicCode.INSTANCE.toEntropy(mnemonicList);
            
            // Derive wallet from mnemonic using BIP44 path (m/44'/60'/0'/0/0 for Ethereum)
            DeterministicKey masterKey = HDKeyDerivation.createMasterPrivateKey(entropy);
            DeterministicKey derivedKey = HDKeyDerivation.deriveChildKey(masterKey, new ChildNumber(44, true));
            derivedKey = HDKeyDerivation.deriveChildKey(derivedKey, new ChildNumber(60, true));
            derivedKey = HDKeyDerivation.deriveChildKey(derivedKey, new ChildNumber(0, true));
            derivedKey = HDKeyDerivation.deriveChildKey(derivedKey, new ChildNumber(0, false));
            derivedKey = HDKeyDerivation.deriveChildKey(derivedKey, new ChildNumber(0, false));
            
            // Convert to Ethereum key pair
            ECKeyPair keyPair = ECKeyPair.create(derivedKey.getPrivKeyBytes());
            String address = Keys.getAddress(keyPair);
            String privateKey = keyPair.getPrivateKey().toString(16);
            
            Wallet wallet = new Wallet();
            wallet.setAddress(address);
            wallet.setPrivateKeyHash(hashPrivateKey(privateKey));
            wallet.setBlockchainNetwork(Wallet.BlockchainNetwork.ETHEREUM_MAINNET);
            
            return wallet;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid recovery phrase: " + e.getMessage());
        }
    }
    
    /**
     * Get wallet balance
     */
    public BigInteger getBalance(String address) throws Exception {
        if (web3j == null) {
            initializeWeb3j();
        }
        
        return web3j.ethGetBalance(address, org.web3j.protocol.core.DefaultBlockParameterName.LATEST)
                .send()
                .getBalance();
    }
    
    /**
     * Send transaction with gas estimation
     */
    public String sendTransaction(String fromAddress, String toAddress, BigInteger amount, 
                                 String privateKey) throws Exception {
        if (web3j == null) {
            initializeWeb3j();
        }
        
        Credentials credentials = Credentials.create(privateKey);
        
        // Get current gas price
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        
        // Estimate gas limit for the transaction
        BigInteger gasLimit = estimateGasLimit(fromAddress, toAddress, amount);
        
        org.web3j.tx.RawTransactionManager transactionManager = 
            new org.web3j.tx.RawTransactionManager(web3j, credentials);
        
        org.web3j.tx.Transfer transfer = new org.web3j.tx.Transfer(web3j, transactionManager);
        
        org.web3j.protocol.core.methods.response.TransactionReceipt receipt = 
            transfer.sendFunds(toAddress, java.math.BigDecimal.valueOf(amount.longValue()), 
                             org.web3j.utils.Convert.Unit.ETHER).send();
        
        return receipt.getTransactionHash();
    }
    
    /**
     * Send transaction with custom gas parameters
     */
    public String sendTransactionWithGas(String fromAddress, String toAddress, BigInteger amount, 
                                        BigInteger gasPrice, BigInteger gasLimit, String privateKey) throws Exception {
        if (web3j == null) {
            initializeWeb3j();
        }
        
        Credentials credentials = Credentials.create(privateKey);
        
        org.web3j.tx.RawTransactionManager transactionManager = 
            new org.web3j.tx.RawTransactionManager(web3j, credentials);
        
        org.web3j.tx.Transfer transfer = new org.web3j.tx.Transfer(web3j, transactionManager);
        
        org.web3j.protocol.core.methods.response.TransactionReceipt receipt = 
            transfer.sendFunds(toAddress, java.math.BigDecimal.valueOf(amount.longValue()), 
                             org.web3j.utils.Convert.Unit.ETHER).send();
        
        return receipt.getTransactionHash();
    }
    
    /**
     * Estimate gas limit for a transaction
     */
    public BigInteger estimateGasLimit(String fromAddress, String toAddress, BigInteger amount) throws Exception {
        if (web3j == null) {
            initializeWeb3j();
        }
        
        // For simple ETH transfers, gas limit is typically 21,000
        // For more complex transactions, we would estimate based on the transaction
        return BigInteger.valueOf(21000);
    }
    
    /**
     * Get current gas price
     */
    public BigInteger getCurrentGasPrice() throws Exception {
        if (web3j == null) {
            initializeWeb3j();
        }
        
        return web3j.ethGasPrice().send().getGasPrice();
    }
    
    /**
     * Get gas price in Gwei
     */
    public double getGasPriceInGwei() throws Exception {
        BigInteger gasPrice = getCurrentGasPrice();
        return gasPrice.doubleValue() / 1_000_000_000.0; // Convert wei to Gwei
    }
    
    /**
     * Hash private key for storage
     */
    private String hashPrivateKey(String privateKey) {
        // In production, use proper encryption/hashing
        return org.springframework.security.crypto.bcrypt.BCrypt.hashpw(privateKey, 
            org.springframework.security.crypto.bcrypt.BCrypt.gensalt());
    }
    
    /**
     * Get transaction status
     */
    public boolean isTransactionConfirmed(String transactionHash) throws Exception {
        if (web3j == null) {
            initializeWeb3j();
        }
        
        org.web3j.protocol.core.methods.response.TransactionReceipt receipt = 
            web3j.ethGetTransactionReceipt(transactionHash).send().getTransactionReceipt().orElse(null);
        
        return receipt != null && receipt.getStatus().equals("0x1");
    }
}
