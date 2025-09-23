package com.springten.wallet.controller;

import com.springten.wallet.dto.TransactionRequest;
import com.springten.wallet.dto.TransactionResponse;
import com.springten.wallet.web3.Web3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    @Autowired
    private Web3Service web3Service;
    
    /**
     * Send a transaction
     */
    @PostMapping("/send")
    public ResponseEntity<TransactionResponse> sendTransaction(@RequestBody TransactionRequest request) {
        try {
            String transactionHash = web3Service.sendTransaction(
                request.getFromAddress(),
                request.getToAddress(),
                request.getAmount(),
                request.getPrivateKey()
            );
            
            TransactionResponse response = new TransactionResponse();
            response.setTransactionHash(transactionHash);
            response.setStatus("PENDING");
            response.setFromAddress(request.getFromAddress());
            response.setToAddress(request.getToAddress());
            response.setAmount(request.getAmount());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TransactionResponse errorResponse = new TransactionResponse();
            errorResponse.setStatus("FAILED");
            errorResponse.setError(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Send transaction with custom gas parameters
     */
    @PostMapping("/send-with-gas")
    public ResponseEntity<TransactionResponse> sendTransactionWithGas(@RequestBody TransactionRequest request) {
        try {
            String transactionHash = web3Service.sendTransactionWithGas(
                request.getFromAddress(),
                request.getToAddress(),
                request.getAmount(),
                request.getGasPrice(),
                request.getGasLimit(),
                request.getPrivateKey()
            );
            
            TransactionResponse response = new TransactionResponse();
            response.setTransactionHash(transactionHash);
            response.setStatus("PENDING");
            response.setFromAddress(request.getFromAddress());
            response.setToAddress(request.getToAddress());
            response.setAmount(request.getAmount());
            response.setGasPrice(request.getGasPrice());
            response.setGasUsed(request.getGasLimit());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            TransactionResponse errorResponse = new TransactionResponse();
            errorResponse.setStatus("FAILED");
            errorResponse.setError(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get current gas price
     */
    @GetMapping("/gas-price")
    public ResponseEntity<BigInteger> getGasPrice() {
        try {
            BigInteger gasPrice = web3Service.getCurrentGasPrice();
            return ResponseEntity.ok(gasPrice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get gas price in Gwei
     */
    @GetMapping("/gas-price-gwei")
    public ResponseEntity<Double> getGasPriceInGwei() {
        try {
            double gasPriceGwei = web3Service.getGasPriceInGwei();
            return ResponseEntity.ok(gasPriceGwei);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Estimate gas limit for a transaction
     */
    @PostMapping("/estimate-gas")
    public ResponseEntity<BigInteger> estimateGas(@RequestBody TransactionRequest request) {
        try {
            BigInteger gasLimit = web3Service.estimateGasLimit(
                request.getFromAddress(),
                request.getToAddress(),
                request.getAmount()
            );
            return ResponseEntity.ok(gasLimit);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Check transaction status
     */
    @GetMapping("/status/{transactionHash}")
    public ResponseEntity<Boolean> getTransactionStatus(@PathVariable String transactionHash) {
        try {
            boolean isConfirmed = web3Service.isTransactionConfirmed(transactionHash);
            return ResponseEntity.ok(isConfirmed);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
