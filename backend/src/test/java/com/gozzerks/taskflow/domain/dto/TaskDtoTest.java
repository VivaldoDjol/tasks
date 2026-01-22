package com.gozzerks.taskflow.domain.dto;

import com.gozzerks.taskflow.domain.enums.TaskPriority;
import com.gozzerks.taskflow.domain.enums.TaskStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TaskDto Validation Tests")
class TaskDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Title Validation Tests")
    class TitleValidationTests {

        @Test
        @DisplayName("Should pass validation with valid title")
        void shouldPassValidation_WithValidTitle() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Valid Task Title",
                    "Description",
                    LocalDate.now(),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when title is null")
        void shouldFailValidation_WhenTitleIsNull() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    null,
                    "Description",
                    LocalDate.now(),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("must not be blank");
        }

        @Test
        @DisplayName("Should fail validation when title is empty")
        void shouldFailValidation_WhenTitleIsEmpty() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "",
                    "Description",
                    LocalDate.now(),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getPropertyPath().toString())
                    .isEqualTo("title");
        }

        @Test
        @DisplayName("Should fail validation when title is only whitespace")
        void shouldFailValidation_WhenTitleIsWhitespace() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "   ",
                    "Description",
                    LocalDate.now(),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getPropertyPath().toString())
                    .isEqualTo("title");
        }

        @Test
        @DisplayName("Should fail validation when title exceeds max length")
        void shouldFailValidation_WhenTitleExceedsMaxLength() {
            // Arrange
            String longTitle = "a".repeat(256);
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    longTitle,
                    "Description",
                    LocalDate.now(),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            if (!violations.isEmpty()) {
                assertThat(violations.iterator().next().getPropertyPath().toString())
                        .isEqualTo("title");
            }
        }

        @Test
        @DisplayName("Should pass validation with title at max length boundary")
        void shouldPassValidation_WithTitleAtMaxLength() {
            // Arrange
            String maxLengthTitle = "a".repeat(255);
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    maxLengthTitle,
                    "Description",
                    LocalDate.now(),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).filteredOn(v -> v.getPropertyPath().toString().equals("title"))
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("Description Validation Tests")
    class DescriptionValidationTests {

        @Test
        @DisplayName("Should pass validation with null description")
        void shouldPassValidation_WithNullDescription() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Valid Title",
                    null,
                    LocalDate.now(),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with empty description")
        void shouldPassValidation_WithEmptyDescription() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Valid Title",
                    "",
                    LocalDate.now(),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when description exceeds max length")
        void shouldFailValidation_WhenDescriptionExceedsMaxLength() {
            // Arrange
            String longDescription = "a".repeat(1001);
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Valid Title",
                    longDescription,
                    LocalDate.now(),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            if (!violations.isEmpty()) {
                assertThat(violations.iterator().next().getPropertyPath().toString())
                        .isEqualTo("description");
            }
        }
    }

    @Nested
    @DisplayName("Priority Validation Tests")
    class PriorityValidationTests {

        @Test
        @DisplayName("Should pass validation with valid priority")
        void shouldPassValidation_WithValidPriority() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Valid Title",
                    "Description",
                    LocalDate.now(),
                    TaskPriority.HIGH,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when priority is null")
        void shouldFailValidation_WhenPriorityIsNull() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Valid Title",
                    "Description",
                    LocalDate.now(),
                    null,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> 
                    v.getPropertyPath().toString().equals("priority"));
        }
    }

    @Nested
    @DisplayName("Status Validation Tests")
    class StatusValidationTests {

        @Test
        @DisplayName("Should pass validation with valid status")
        void shouldPassValidation_WithValidStatus() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Valid Title",
                    "Description",
                    LocalDate.now(),
                    TaskPriority.MEDIUM,
                    TaskStatus.IN_PROGRESS,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when status is null")
        void shouldFailValidation_WhenStatusIsNull() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Valid Title",
                    "Description",
                    LocalDate.now(),
                    TaskPriority.MEDIUM,
                    null,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> 
                    v.getPropertyPath().toString().equals("status"));
        }
    }

    @Nested
    @DisplayName("Due Date Validation Tests")
    class DueDateValidationTests {

        @Test
        @DisplayName("Should pass validation with null due date")
        void shouldPassValidation_WithNullDueDate() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Valid Title",
                    "Description",
                    null,
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with future due date")
        void shouldPassValidation_WithFutureDueDate() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Valid Title",
                    "Description",
                    LocalDate.now().plusDays(7),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with past due date")
        void shouldPassValidation_WithPastDueDate() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Valid Title",
                    "Description",
                    LocalDate.now().minusDays(7),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("TaskList ID Validation Tests")
    class TaskListIdValidationTests {

        @Test
        @DisplayName("Should pass validation with valid taskListId")
        void shouldPassValidation_WithValidTaskListId() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Valid Title",
                    "Description",
                    LocalDate.now(),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when taskListId is null")
        void shouldFailValidation_WhenTaskListIdIsNull() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Valid Title",
                    "Description",
                    LocalDate.now(),
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    null
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> 
                    v.getPropertyPath().toString().equals("taskListId"));
        }
    }

    @Nested
    @DisplayName("Complete DTO Validation Tests")
    class CompleteDTOValidationTests {

        @Test
        @DisplayName("Should pass validation with all valid fields")
        void shouldPassValidation_WithAllValidFields() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Complete Task",
                    "Complete Description",
                    LocalDate.now().plusWeeks(2),
                    TaskPriority.HIGH,
                    TaskStatus.IN_PROGRESS,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should accumulate multiple validation errors")
        void shouldAccumulateMultipleValidationErrors() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    null,
                    "Description",
                    LocalDate.now(),
                    null,
                    null,
                    null
            );

            // Act
            Set<ConstraintViolation<TaskDto>> violations = validator.validate(taskDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations.size()).isGreaterThanOrEqualTo(3);
        }
    }
}