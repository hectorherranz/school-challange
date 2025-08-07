package com.hectorherranz.schoolapi.domain.model.valueobject;

/** Invariant-enforcing value object (50–2000). */
public record Capacity(int value) {
    public Capacity {
        if (value < 50 || value > 2000)
            throw new IllegalArgumentException("Capacity must be between 50 and 2000");
    }
    public boolean canEnroll(int current) { return current < value; }
    public int availableSpots(int current) { return value - current; }
}
