package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TransactionListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(Transaction transaction) {
        
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        if (sender != null && recipient != null) {
            if (sender.getBalance() >= transaction.getAmount()) {
                
                // 1. Call the external Incentive API
                org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                com.jpmc.midascore.foundation.Incentive incentiveObj = restTemplate.postForObject(
                        "http://localhost:8080/incentive", 
                        transaction, 
                        com.jpmc.midascore.foundation.Incentive.class
                );
                
                // Extract the amount, default to 0 if the API fails
                float incentiveAmount = (incentiveObj != null) ? incentiveObj.getAmount() : 0.0f;

                // 2. Update balances (Incentive goes to recipient only)
                sender.setBalance(sender.getBalance() - transaction.getAmount());
                recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);

                userRepository.save(sender);
                userRepository.save(recipient);

                // 3. Record the transaction with the new incentive parameter
                TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
                transactionRecordRepository.save(record);

                // Plan B: Log Wilbur's balance
                if ("wilbur".equalsIgnoreCase(sender.getName())) {
                    System.out.println("🎯 WILBUR BALANCE: " + sender.getBalance());
                }
                if ("wilbur".equalsIgnoreCase(recipient.getName())) {
                    System.out.println("🎯 WILBUR BALANCE: " + recipient.getBalance());
                }
            }
        }
    }
    }
