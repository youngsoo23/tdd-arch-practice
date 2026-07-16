package com.study.tddarchpractice.mock;

import com.study.tddarchpractice.common.service.port.ClockHolder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestClockHolder implements ClockHolder {
    private final long millis;

    @Override
    public long currentTimeMillis() {
        return millis;
    }
}
