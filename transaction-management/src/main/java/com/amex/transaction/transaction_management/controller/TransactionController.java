package com.amex.transaction.transaction_management.controller;

import com.amex.transaction.transaction_management.dto.TransactionDTO;
import com.amex.transaction.transaction_management.dto.TransactionResponseDTO;
import com.amex.transaction.transaction_management.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        TransactionResponseDTO createdTransaction = transactionService.createTransaction(transactionDTO);
        return ResponseEntity.ok(createdTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(@PathVariable Long id,
                                                                    @RequestBody TransactionDTO transactionDTO) {
        TransactionResponseDTO updatedTransaction = transactionService.updateTransaction(id, transactionDTO);
        return ResponseEntity.ok(updatedTransaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransaction(@PathVariable Long id) {
        TransactionDTO transactionDTO = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transactionDTO);
    }

    @GetMapping("/account/{accountNo}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccountNo(@PathVariable Long accountNo) {
        List<TransactionDTO> transactionDTOs = transactionService.getTransactionsByAccountNo(accountNo);
        return ResponseEntity.ok(transactionDTOs);
    }

}
