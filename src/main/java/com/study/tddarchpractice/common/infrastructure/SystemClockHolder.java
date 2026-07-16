package com.study.tddarchpractice.common.infrastructure;

import com.study.tddarchpractice.common.service.port.ClockHolder;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class SystemClockHolder implements ClockHolder {
    @Override
    public long currentTimeMillis() {
        return Clock.systemUTC().millis();
    }
}
