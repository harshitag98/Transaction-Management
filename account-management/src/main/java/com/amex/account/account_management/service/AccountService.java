package com.amex.account.account_management.service;

import com.amex.account.account_management.dao.AccountRepository;
import com.amex.account.account_management.dto.AccountDTO;
import com.amex.account.account_management.dto.AccountResponseDTO;
import com.amex.account.account_management.dto.TransactionDTO;
import com.amex.account.account_management.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public AccountResponseDTO processTransaction(TransactionDTO transactionDTO) {
        AccountDTO accountDTO = getAccountByNumber(transactionDTO.getAccountNo());
        Double balance = accountDTO.getBalance();
        if (transactionDTO.getTransactionType().equals("DEBIT")) {
            if (balance >= transactionDTO.getAmount()) {
                accountDTO.setBalance(balance - transactionDTO.getAmount());
            } else {
                return AccountResponseDTO.builder()
                        .status(Boolean.FALSE)
                        .message("Insufficient balance!!").build();
            }
        } else if (transactionDTO.getTransactionType().equals("CREDIT")) {
            accountDTO.setBalance(balance + transactionDTO.getAmount());
        }

        AccountDTO savedAccountDTO = saveAccountDetails(accountDTO);

        return AccountResponseDTO.builder()
                .status(Boolean.FALSE)
                .message("Transaction successful.")
                .build();
    }

    private AccountDTO saveAccountDetails(AccountDTO accountDTO) {
        Account account = mapToAccount(accountDTO);
        return mapToDTO(accountRepository.save(account));
    }

    public AccountDTO getAccountByNumber(Long accountNumber) {
        Optional<Account> accountOptional = accountRepository.findByAccountNo(accountNumber);
        return mapToDTO(accountOptional.get());
    }

    private Account mapToAccount(AccountDTO accountDTO) {
        Account account = new Account();
        account.setId(accountDTO.getId());
        account.setAccountNo(accountDTO.getAccountNo());
        account.setBalance(accountDTO.getBalance());
        return account;
    }

    private AccountDTO mapToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setAccountNo(account.getAccountNo());
        dto.setBalance(account.getBalance());
        return dto;
    }
}
