package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.command.UpdateSchoolCommand;
import com.hectorherranz.schoolapi.application.port.in.UpdateSchoolUseCase;
import com.hectorherranz.schoolapi.domain.exception.DuplicateNameException;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class UpdateSchoolHandler implements UpdateSchoolUseCase {

    private final SchoolRepositoryPort schoolRepository;

    public UpdateSchoolHandler(SchoolRepositoryPort schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Override
    public void handle(UpdateSchoolCommand command) {
        // Find the school
        School school = schoolRepository.findById(command.schoolId())
                .orElseThrow(() -> new NotFoundException("School", command.schoolId().toString()));

        // Update name if provided
        command.name().ifPresent(newName -> {
            // Check for duplicate name (excluding current school)
            if (!newName.equalsIgnoreCase(school.name()) && 
                schoolRepository.existsByNameIgnoreCase(newName)) {
                throw new DuplicateNameException();
            }
            school.changeName(newName);
        });

        // Update capacity if provided
        command.capacity().ifPresent(newCapacity -> {
            Capacity capacity = new Capacity(newCapacity);
            school.resizeCapacity(capacity);
        });

        // Save the updated school
        schoolRepository.save(school);
    }
}
