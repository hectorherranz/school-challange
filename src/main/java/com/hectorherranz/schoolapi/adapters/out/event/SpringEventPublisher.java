package com.hectorherranz.schoolapi.adapters.out.event;

import com.hectorherranz.schoolapi.application.port.DomainEventPublisher;
import com.hectorherranz.schoolapi.domain.event.DomainEvent;
import java.util.List;

public class SpringEventPublisher implements DomainEventPublisher {
    @Override public void publish(List<DomainEvent> events) { /* TODO */ }
}
