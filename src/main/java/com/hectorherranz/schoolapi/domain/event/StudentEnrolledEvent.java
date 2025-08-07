package com.hectorherranz.schoolapi.domain.event;

public record StudentEnrolledEvent(java.util.UUID studentId, java.util.UUID schoolId)
    implements DomainEvent {}
