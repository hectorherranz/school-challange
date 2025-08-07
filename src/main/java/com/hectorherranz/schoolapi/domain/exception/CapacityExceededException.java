package com.hectorherranz.schoolapi.domain.exception;

import java.util.UUID;

/**
 * Thrown when an attempt is made to enrol a student in a school
 * that has already reached its maximum capacity.
 *
 * Lives in the <strong>domain</strong> layer so the aggregate can raise it
 * without depending on infrastructure concerns.
 */
public class CapacityExceededException extends RuntimeException {

    private final UUID schoolId;

    /**
     * @param schoolId the identifier of the school that is full
     */
    public CapacityExceededException(UUID schoolId) {
        super("School %s is at maximum capacity".formatted(schoolId));
        this.schoolId = schoolId;
    }

    /** Exposes the offending school ID for Problem-Details mapping, logging, etc. */
    public UUID schoolId() {
        return schoolId;
    }
}
