package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.command.CreateStudentCommand;
import com.hectorherranz.schoolapi.application.port.in.CreateStudentUseCase;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.School;
import com.hectorherranz.schoolapi.domain.model.Student;
import com.hectorherranz.schoolapi.domain.model.draft.StudentDraft;
import com.hectorherranz.schoolapi.domain.repository.SchoolRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Transactional
public class CreateStudentHandler implements CreateStudentUseCase {

    private final SchoolRepositoryPort schoolRepository;

    public CreateStudentHandler(SchoolRepositoryPort schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @Override
    public UUID handle(CreateStudentCommand command) {
        // Find the school
        School school = schoolRepository.findById(command.schoolId())
                .orElseThrow(() -> new NotFoundException("School", command.schoolId().toString()));

        // Create student draft
        StudentDraft draft = new StudentDraft(command.name());

        // Enroll student through the school aggregate
        Student student = school.enrollStudent(draft);

        // Save the updated school (which includes the new student)
        schoolRepository.save(school);

        return student.id();
    }
}
