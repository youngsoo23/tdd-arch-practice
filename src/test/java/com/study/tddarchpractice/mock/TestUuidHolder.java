package com.study.tddarchpractice.mock;

import com.study.tddarchpractice.common.service.port.UuidHolder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestUuidHolder implements UuidHolder {
    private final String uuid;

    @Override
    public String generateUuid() {
        return uuid;
    }
}
