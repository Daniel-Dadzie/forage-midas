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
        
        // 1. Fetch both users from the database (Direct return, no Optional wrapper)
        UserRecord sender = userRepository.findById(transaction.getSenderId());
        UserRecord recipient = userRepository.findById(transaction.getRecipientId());

        // 2. Validate users exist (Standard null check)
        if (sender != null && recipient != null) {

        // 3. Validate sufficient funds
        if (sender.getBalance() >= transaction.getAmount()) {
                
                // 4. Update balances
                sender.setBalance(sender.getBalance() - transaction.getAmount());
                recipient.setBalance(recipient.getBalance() + transaction.getAmount());

                // 5. Save updated users
                userRepository.save(sender);
                userRepository.save(recipient);

                // 6. Record the transaction
                TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount());
                transactionRecordRepository.save(record);

                // Plan B: Log Waldorf's balance so we can find our answer
                if ("waldorf".equalsIgnoreCase(sender.getName())) {
                    System.out.println("🎯 WALDORF BALANCE: " + sender.getBalance());
                }
                if ("waldorf".equalsIgnoreCase(recipient.getName())) {
                    System.out.println("🎯 WALDORF BALANCE: " + recipient.getBalance());
                }
            }    } 
        }
    }
