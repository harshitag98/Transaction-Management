package com.amex.transaction.transaction_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private String transactionType;
    private Double amount;
    private Long accountNo;
    private LocalDateTime transactionDate;
}
