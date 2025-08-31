package com.rolly.practice.hospitalmgt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.awt.desktop.SystemEventListener;

@Service
public class EmailConsumerService {

    @Autowired
    //private EmailService emailService;

    @KafkaListener(topics = "appointment-email", groupId = "appointment-email-group")
    public void consume() {
        sendEmail();
    }

    private void sendEmail() {
        //emailService.sendEmail("camroonrolly@gmail.com", "Hospital Management test email", "test body");
        System.out.println("email sent");
    }
}