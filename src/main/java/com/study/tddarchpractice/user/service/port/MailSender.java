package com.study.tddarchpractice.user.service.port;

public interface MailSender {

    void send(String email, String subject, String content);
}
