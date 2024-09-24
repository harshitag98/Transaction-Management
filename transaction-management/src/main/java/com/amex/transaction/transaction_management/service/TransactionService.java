package com.amex.transaction.transaction_management.service;

import com.amex.transaction.transaction_management.dao.TransactionRepository;
import com.amex.transaction.transaction_management.dto.AccountResponseDTO;
import com.amex.transaction.transaction_management.dto.TransactionDTO;
import com.amex.transaction.transaction_management.dto.TransactionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.amex.transaction.transaction_management.entity.Transaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private KafkaTemplate<String, TransactionDTO> kafkaTemplate;

    @Value("${process.transaction.api}")
    private String processTransactionApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public TransactionResponseDTO createTransaction(TransactionDTO transactionDTO) {

        AccountResponseDTO accountResponseDTO = processTransaction(transactionDTO);
        if (Objects.isNull(accountResponseDTO)) {
            return TransactionResponseDTO.builder()
                    .status(Boolean.FALSE)
                    .message(accountResponseDTO.getMessage())
                    .build();
        }
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDTO.getTransactionType())
                .amount(transactionDTO.getAmount())
                .accountNo(transactionDTO.getAccountNo())
                .transactionDate(LocalDateTime.now())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        sendToKafkaTopic(transactionDTO);

        return TransactionResponseDTO.builder()
                .status(Boolean.TRUE)
                .message("Transaction successful!")
                .transactionDTO(mapToDTO(savedTransaction))
                .build();
    }

    private AccountResponseDTO processTransaction(TransactionDTO transactionDTO) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TransactionDTO> httpEntity = new HttpEntity<>(transactionDTO, httpHeaders);

        try {
            ResponseEntity<AccountResponseDTO> response = restTemplate.exchange(
                    processTransactionApiUrl, HttpMethod.PUT, httpEntity, AccountResponseDTO.class);
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    private void sendToKafkaTopic(TransactionDTO transactionDTO) {
        kafkaTemplate.send("transaction-topic", transactionDTO);
    }

    @Transactional
    public TransactionResponseDTO updateTransaction(Long id, TransactionDTO transactionDTO) {
        Optional<Transaction> transactionOptional = transactionRepository.findById(id);
        if (transactionOptional.isEmpty()) {
            return TransactionResponseDTO.builder()
                    .status(Boolean.FALSE)
                    .message("There is no transaction for id : " + id)
                    .build();
        }
        Transaction transaction = transactionOptional.get();
        transaction.setTransactionType(transactionDTO.getTransactionType());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setAccountNo(transactionDTO.getAccountNo());
        transaction.setTransactionDate(transactionDTO.getTransactionDate());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        return TransactionResponseDTO.builder()
                .status(Boolean.TRUE)
                .message("Transaction updated successfully for id : " + id)
                .transactionDTO(mapToDTO(updatedTransaction))
                .build();
    }

    public TransactionDTO getTransactionById(Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        return mapToDTO(transaction.get());
    }

    public List<TransactionDTO> getTransactionsByAccountNo(Long accountNo) {
        List<Transaction> transactions = transactionRepository.findByAccountNo(accountNo);
        return transactions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private TransactionDTO mapToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setAmount(transaction.getAmount());
        dto.setAccountNo(transaction.getAccountNo());
        dto.setTransactionDate(transaction.getTransactionDate());
        return dto;
    }
}
