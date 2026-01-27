package com.gozzerks.taskflow.domain.dto;

import com.gozzerks.taskflow.domain.enums.Priority;
import com.gozzerks.taskflow.domain.enums.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateTaskDto Validation Tests")
class CreateTaskDtoTest {

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
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Task Title",
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when title is null")
        void shouldFailValidation_WhenTitleIsNull() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    null,
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

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
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "",
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getPropertyPath().toString())
                    .isEqualTo("title");
        }

        @Test
        @DisplayName("Should fail validation when title is only whitespace")
        void shouldFailValidation_WhenTitleIsWhitespace() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "   ",
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

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
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    longTitle,
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getPropertyPath().toString())
                    .isEqualTo("title");
        }

        @Test
        @DisplayName("Should pass validation with title at max length boundary")
        void shouldPassValidation_WithTitleAtMaxLength() {
            // Arrange
            String maxLengthTitle = "a".repeat(255);
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    maxLengthTitle,
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).filteredOn(v -> v.getPropertyPath().toString().equals("title"))
                    .isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with special characters in title")
        void shouldPassValidation_WithSpecialCharactersInTitle() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Café ☀️ naïve résumé",
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Description Validation Tests")
    class DescriptionValidationTests {

        @Test
        @DisplayName("Should pass validation with null description")
        void shouldPassValidation_WithNullDescription() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    null,
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with empty description")
        void shouldPassValidation_WithEmptyDescription() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when description exceeds max length")
        void shouldFailValidation_WhenDescriptionExceedsMaxLength() {
            // Arrange
            String longDescription = "a".repeat(1001);
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    longDescription,
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getPropertyPath().toString())
                    .isEqualTo("description");
        }

        @Test
        @DisplayName("Should pass validation with description at max length boundary")
        void shouldPassValidation_WithDescriptionAtMaxLength() {
            // Arrange
            String maxLengthDescription = "a".repeat(1000);
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    maxLengthDescription,
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).filteredOn(v -> v.getPropertyPath().toString().equals("description"))
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("Priority Validation Tests")
    class PriorityValidationTests {

        @Test
        @DisplayName("Should pass validation with null priority")
        void shouldPassValidation_WithNullPriority() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "Description",
                    null,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with LOW priority")
        void shouldPassValidation_WithLowPriority() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "Description",
                    Priority.LOW,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with MEDIUM priority")
        void shouldPassValidation_WithMediumPriority() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with HIGH priority")
        void shouldPassValidation_WithHighPriority() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "Description",
                    Priority.HIGH,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Status Validation Tests")
    class StatusValidationTests {

        @Test
        @DisplayName("Should pass validation with null status")
        void shouldPassValidation_WithNullStatus() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "Description",
                    Priority.MEDIUM,
                    null,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with TODO status")
        void shouldPassValidation_WithTodoStatus() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with IN_PROGRESS status")
        void shouldPassValidation_WithInProgressStatus() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "Description",
                    Priority.MEDIUM,
                    Status.IN_PROGRESS,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with DONE status")
        void shouldPassValidation_WithDoneStatus() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "Description",
                    Priority.MEDIUM,
                    Status.DONE,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Due Date Validation Tests")
    class DueDateValidationTests {

        @Test
        @DisplayName("Should pass validation with null due date")
        void shouldPassValidation_WithNullDueDate() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    null,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with future due date")
        void shouldPassValidation_WithFutureDueDate() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(30),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with past due date")
        void shouldPassValidation_WithPastDueDate() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().minusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with current date time")
        void shouldPassValidation_WithCurrentDateTime() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now(),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("TaskList ID Validation Tests")
    class TaskListIdValidationTests {

        @Test
        @DisplayName("Should pass validation with valid taskList ID")
        void shouldPassValidation_WithValidTaskListId() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when taskList ID is null")
        void shouldFailValidation_WhenTaskListIdIsNull() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    "Description",
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    null
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getPropertyPath().toString())
                    .isEqualTo("taskListId");
        }
    }

    @Nested
    @DisplayName("Complete DTO Validation Tests")
    class CompleteDTOValidationTests {

        @Test
        @DisplayName("Should pass validation with all valid fields")
        void shouldPassValidation_WithAllValidFields() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Complete Task",
                    "Complete Description",
                    Priority.HIGH,
                    Status.IN_PROGRESS,
                    LocalDateTime.now().plusDays(14),
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with minimal required fields")
        void shouldPassValidation_WithMinimalRequiredFields() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    "Valid Title",
                    null,
                    null,
                    null,
                    null,
                    UUID.randomUUID()
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should accumulate multiple validation errors")
        void shouldAccumulateMultipleValidationErrors() {
            // Arrange
            CreateTaskDto createTaskDto = new CreateTaskDto(
                    null,
                    "a".repeat(1001),
                    Priority.MEDIUM,
                    Status.TODO,
                    LocalDateTime.now().plusDays(7),
                    null
            );

            // Act
            Set<ConstraintViolation<CreateTaskDto>> violations = validator.validate(createTaskDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations.size()).isGreaterThanOrEqualTo(2);
        }
    }
}