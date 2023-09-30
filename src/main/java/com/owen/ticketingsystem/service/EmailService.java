package com.owen.ticketingsystem.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendNewPasswordEmail(String firstName, String newPassword, String email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(email);
        helper.setSubject("New Password");
        helper.setText("Hi " + firstName + ",\n\nYour new password is: " + newPassword + "\n\nThe Support Team");

        mailSender.send(message);
    }
    public void sendResetLink(String firstName, String resetLink, String email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(email);
        helper.setSubject("重設密碼鏈結");
        helper.setText("Hi " + firstName + ",\n\n請重設密碼\n" + resetLink + "\n\n職業棒球售票網站");

        mailSender.send(message);
    }
}
