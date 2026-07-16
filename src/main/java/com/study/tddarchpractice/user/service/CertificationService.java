package com.study.tddarchpractice.user.service;

import com.study.tddarchpractice.user.infrastructure.UserEntity;
import com.study.tddarchpractice.user.service.port.MailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CertificationService {
    private final MailSender mailSender;

    public void sendCertificationEmail(String email, long userId, String certificationUrl) {
        String subject = "Please certify your email address";
        String text = "Please click the following link to certify your email address: " + certificationUrl;
        mailSender.send(email, subject, text);
    }

    public String generateCertificationUrl(long userId, String certificationCode) {
        return "http://localhost:8080/api/users/" + userId + "/verify?certificationCode=" + certificationCode;
    }
}
