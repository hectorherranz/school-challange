package com.hectorherranz.schoolapi.domain.model;

import com.hectorherranz.schoolapi.domain.event.StudentEnrolledEvent;
import com.hectorherranz.schoolapi.domain.exception.CapacityExceededException;
import com.hectorherranz.schoolapi.domain.model.draft.StudentDraft;
import com.hectorherranz.schoolapi.domain.model.valueobject.Capacity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class SchoolTest {

    @Test
    @DisplayName("enrollStudent throws CapacityExceededException when school is full")
    void shouldFailWhenCapacityReached() {
        // given
        int capacity = 50;
        School school = new School(
                UUID.randomUUID(),
                "DDD High School",
                new Capacity(capacity)           // VO
        );

        // pre-fill to max capacity
        for (int i = 0; i < capacity; i++) {
            school.enrollStudent(new StudentDraft("Student" + i));
        }

        // when / then
        assertThatThrownBy(() -> school.enrollStudent(new StudentDraft("Charlie")))
                .isInstanceOf(CapacityExceededException.class)
                .hasMessageContaining("maximum capacity");
    }

    @Test
    @DisplayName("enrollStudent returns new student and registers domain event")
    void shouldEnrollAndRaiseEvent() {
        School school = new School(UUID.randomUUID(), "Event Academy", new Capacity(50));

        Student student = school.enrollStudent(new StudentDraft("Dana"));

        // basic assertions
        assertThat(student.name()).isEqualTo("Dana");
        assertThat(school.enrolledCount()).isEqualTo(1);

        // pulled events contain StudentEnrolledEvent with correct IDs
        var events = school.pullDomainEvents();
        assertThat(events).hasSize(1)
                          .first()
                          .isInstanceOf(StudentEnrolledEvent.class)
                          .extracting("studentId", "schoolId")
                          .containsExactly(student.id(), school.id());
    }
}
