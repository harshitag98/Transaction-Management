package com.amex.account.account_management.controller;

import com.amex.account.account_management.dto.AccountResponseDTO;
import com.amex.account.account_management.dto.TransactionDTO;
import com.amex.account.account_management.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PutMapping
    public ResponseEntity<AccountResponseDTO> processTransaction(@RequestBody TransactionDTO transactionDTO) {
        AccountResponseDTO accountResponseDTO = accountService.processTransaction(transactionDTO);
        return ResponseEntity.ok(accountResponseDTO);
    }
}
