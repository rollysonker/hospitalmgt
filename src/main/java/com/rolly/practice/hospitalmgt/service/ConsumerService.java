package com.rolly.practice.hospitalmgt.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "appointment-mails", groupId = "appointment-mails-group")
    public void consume() {
        sendEmail();
    }

    private void sendEmail() {
        emailService.sendEmail("camroonrolly@gmail.com", "Hospital Management test email", "test body");
    }
}