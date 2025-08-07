package com.hectorherranz.schoolapi.application.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

/**
 * PATCH-style command: the Optional fields are present only when the caller wants to change them.
 */
public record UpdateSchoolCommand(
    @NotNull UUID schoolId, @Valid Optional<String> name, @Valid Optional<Integer> capacity) {}
