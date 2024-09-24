package com.amex.notification.notification_management.consumer;

import com.amex.notification.notification_management.dto.TransactionDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationHandler {

    @KafkaListener(topics = "transaction-topic", groupId = "notification-group")
    public void listen(TransactionDTO transactionDTO) {
        // Logic for sending notifications (email, SMS, etc.)
        System.out.println("Notification sent for Transaction ID: " + transactionDTO.getId());
    }
}
