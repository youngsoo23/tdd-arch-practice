package com.study.tddarchpractice.mock;

import com.study.tddarchpractice.user.service.port.MailSender;

public class FakeMailSenderTest implements MailSender {

    public String email;
    public String subject;
    public String content;

    @Override
    public void send(String email, String subject, String content) {
        this.email = email;
        this.subject = subject;
        this.content = content;
    }
}
