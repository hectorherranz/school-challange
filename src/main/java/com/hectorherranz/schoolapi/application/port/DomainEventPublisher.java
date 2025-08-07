package com.hectorherranz.schoolapi.application.port;

import com.hectorherranz.schoolapi.domain.event.DomainEvent;
import java.util.List;

public interface DomainEventPublisher {
    void publish(List<DomainEvent> events);
}
