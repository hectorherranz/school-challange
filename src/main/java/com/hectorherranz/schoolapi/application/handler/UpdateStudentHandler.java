package com.hectorherranz.schoolapi.application.handler;

import com.hectorherranz.schoolapi.application.command.UpdateStudentCommand;
import com.hectorherranz.schoolapi.application.port.in.UpdateStudentUseCase;
import com.hectorherranz.schoolapi.domain.exception.NotFoundException;
import com.hectorherranz.schoolapi.domain.model.Student;
import com.hectorherranz.schoolapi.domain.repository.StudentRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class UpdateStudentHandler implements UpdateStudentUseCase {

    private final StudentRepositoryPort studentRepository;

    public UpdateStudentHandler(StudentRepositoryPort studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public void handle(UpdateStudentCommand command) {
        // Find the student
        Student student = studentRepository.findById(command.studentId())
                .orElseThrow(() -> new NotFoundException("Student", command.studentId().toString()));

        // Create updated student with new name
        Student updatedStudent = new Student(
                student.id(),
                command.name(),
                student.schoolId()
        );

        // Save the updated student
        studentRepository.save(updatedStudent);
    }
}
