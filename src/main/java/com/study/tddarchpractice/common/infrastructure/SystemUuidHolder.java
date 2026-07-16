package com.study.tddarchpractice.common.infrastructure;

import com.study.tddarchpractice.common.service.port.UuidHolder;
import org.springframework.stereotype.Component;

import static java.util.UUID.randomUUID;

@Component
public class SystemUuidHolder implements UuidHolder {
    @Override
    public String generateUuid() {
        return randomUUID().toString();
    }
}
