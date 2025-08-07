package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.command.CreateSchoolCommand;
import com.hectorherranz.schoolapi.application.port.in.CreateSchoolUseCase;
import com.hectorherranz.schoolapi.domain.exception.DuplicateNameException;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional
public class CreateSchoolHandler implements CreateSchoolUseCase {

    private final SchoolRepositoryPort schoolRepository;

    public CreateSchoolHandler(SchoolRepositoryPort schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Override
    public UUID handle(CreateSchoolCommand command) {
        // Check for duplicate name
        if (schoolRepository.existsByNameIgnoreCase(command.name())) {
            throw new DuplicateNameException();
        }

        // Create domain value objects
        Capacity capacity = new Capacity(command.capacity());

        // Create the school aggregate
        School school = new School(UUID.randomUUID(), command.name(), capacity);

        // Save and return the ID
        School savedSchool = schoolRepository.save(school);
        return savedSchool.id();
    }
}
