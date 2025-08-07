package com.hectorherranz.schoolapi.domain.model;

import com.hectorherranz.schoolapi.domain.event.DomainEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {
    protected final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void registerEvent(DomainEvent event) { /* TODO */ }
    public List<DomainEvent> pullDomainEvents()     { return null; }  // TODO
}
