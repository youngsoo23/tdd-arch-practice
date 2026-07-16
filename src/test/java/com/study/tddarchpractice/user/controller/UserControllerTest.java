package com.study.tddarchpractice.user.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:test-application.yml")
@SqlGroup({
        @Sql(scripts = "/sql/user-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "/sql/clear-repository-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getUserById는_ACTIVE_상태의_유저를_조회할수있다() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nickname").value("ohyoungsoo"));
    }

    @Test
    void getUserById는_PENDING_상태의_유저를_조회할수없다() throws Exception {
        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void verifyEmail은_인증코드가_일치하면_302로_응답한다() throws Exception {
        mockMvc.perform(get("/api/users/2/verify")
                        .param("certificationCode", "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "http://localhost:3000"));
    }

    @Test
    void verifyEmail은_인증코드가_일치하지않으면_403으로_응답한다() throws Exception {
        mockMvc.perform(get("/api/users/2/verify")
                        .param("certificationCode", "wrong-code"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyInfo는_EMAIL_헤더로_내정보를_조회할수있다() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("EMAIL", "oh.youngsoo23@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("ohyoungsoo"))
                .andExpect(jsonPath("$.address").value("Seoul, South Korea"));
    }

    @Test
    void updateMyInfo는_내정보를_수정할수있다() throws Exception {
        mockMvc.perform(put("/api/users/me")
                        .header("EMAIL", "oh.youngsoo23@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nickname": "ohyoungsoo12",
                                    "address": "Seoul Nowon"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("ohyoungsoo12"))
                .andExpect(jsonPath("$.address").value("Seoul Nowon"));
    }
}
