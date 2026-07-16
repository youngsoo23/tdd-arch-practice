package com.study.tddarchpractice.user.service;


import com.study.tddarchpractice.mock.FakeMailSenderTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CertificationServiceTest {

    @Test
    public void 이메일과_컨텐츠가_정상적으로_전송되는지_확인() {
        // given
        FakeMailSenderTest fakeMailSenderTest = new FakeMailSenderTest();
        CertificationService certificationService = new CertificationService(fakeMailSenderTest);
        // when
        certificationService.send("oh.youngsoo23@gmail.com", 1L, "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        // then
        assertThat(fakeMailSenderTest.email).isEqualTo("oh.youngsoo23@gmail.com");
        assertThat(fakeMailSenderTest.subject).isEqualTo("Please certify your email address");
        assertThat(fakeMailSenderTest.content).isEqualTo("Please click the following link to certify your email address: http://localhost:8080/api/users/1/verify?certificationCode=aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        }
}