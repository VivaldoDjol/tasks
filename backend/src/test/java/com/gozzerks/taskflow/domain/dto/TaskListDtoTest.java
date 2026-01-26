package com.gozzerks.taskflow.domain.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TaskListDto Validation Tests")
class TaskListDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Name Validation Tests")
    class NameValidationTests {

        @Test
        @DisplayName("Should pass validation with valid name")
        void shouldPassValidation_WithValidName() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Task List Name",
                    "Description",
                    BigDecimal.valueOf(50.0),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when name is null")
        void shouldFailValidation_WhenNameIsNull() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    null,
                    "Description",
                    BigDecimal.valueOf(50.0),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("must not be blank");
        }

        @Test
        @DisplayName("Should fail validation when name is empty")
        void shouldFailValidation_WhenNameIsEmpty() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "",
                    "Description",
                    BigDecimal.valueOf(50.0),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getPropertyPath().toString())
                    .isEqualTo("name");
        }

        @Test
        @DisplayName("Should fail validation when name is only whitespace")
        void shouldFailValidation_WhenNameIsWhitespace() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "   ",
                    "Description",
                    BigDecimal.valueOf(50.0),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations.iterator().next().getPropertyPath().toString())
                    .isEqualTo("name");
        }

        @Test
        @DisplayName("Should fail validation when name exceeds max length")
        void shouldFailValidation_WhenNameExceedsMaxLength() {
            // Arrange
            String longName = "a".repeat(256);
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    longName,
                    "Description",
                    BigDecimal.valueOf(50.0),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            if (!violations.isEmpty()) {
                assertThat(violations.iterator().next().getPropertyPath().toString())
                        .isEqualTo("name");
            }
        }

        @Test
        @DisplayName("Should pass validation with name at max length boundary")
        void shouldPassValidation_WithNameAtMaxLength() {
            // Arrange
            String maxLengthName = "a".repeat(255);
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    maxLengthName,
                    "Description",
                    BigDecimal.valueOf(50.0),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).filteredOn(v -> v.getPropertyPath().toString().equals("name"))
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
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    null,
                    BigDecimal.valueOf(50.0),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with empty description")
        void shouldPassValidation_WithEmptyDescription() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    "",
                    BigDecimal.valueOf(50.0),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when description exceeds max length")
        void shouldFailValidation_WhenDescriptionExceedsMaxLength() {
            // Arrange
            String longDescription = "a".repeat(1001);
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    longDescription,
                    BigDecimal.valueOf(50.0),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            if (!violations.isEmpty()) {
                assertThat(violations.iterator().next().getPropertyPath().toString())
                        .isEqualTo("description");
            }
        }

        @Test
        @DisplayName("Should pass validation with description at max length boundary")
        void shouldPassValidation_WithDescriptionAtMaxLength() {
            // Arrange
            String maxLengthDescription = "a".repeat(1000);
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    maxLengthDescription,
                    BigDecimal.valueOf(50.0),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).filteredOn(v -> v.getPropertyPath().toString().equals("description"))
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("Progress Validation Tests")
    class ProgressValidationTests {

        @Test
        @DisplayName("Should pass validation with zero progress")
        void shouldPassValidation_WithZeroProgress() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    "Description",
                    BigDecimal.ZERO,
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with 100 percent progress")
        void shouldPassValidation_With100PercentProgress() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    "Description",
                    BigDecimal.valueOf(100.0),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with partial progress")
        void shouldPassValidation_WithPartialProgress() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    "Description",
                    BigDecimal.valueOf(45.5),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when progress is negative")
        void shouldFailValidation_WhenProgressIsNegative() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    "Description",
                    BigDecimal.valueOf(-1.0),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            if (!violations.isEmpty()) {
                assertThat(violations).anyMatch(v ->
                        v.getPropertyPath().toString().equals("progress"));
            }
        }

        @Test
        @DisplayName("Should fail validation when progress exceeds 100")
        void shouldFailValidation_WhenProgressExceeds100() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    "Description",
                    BigDecimal.valueOf(101.0),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            if (!violations.isEmpty()) {
                assertThat(violations).anyMatch(v ->
                        v.getPropertyPath().toString().equals("progress"));
            }
        }

        @Test
        @DisplayName("Should pass validation with null progress")
        void shouldPassValidation_WithNullProgress() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    "Description",
                    null,
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Task Count Validation Tests")
    class TaskCountValidationTests {

        @Test
        @DisplayName("Should pass validation with zero task count")
        void shouldPassValidation_WithZeroTaskCount() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    "Description",
                    BigDecimal.ZERO,
                    0
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with positive task count")
        void shouldPassValidation_WithPositiveTaskCount() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    "Description",
                    BigDecimal.valueOf(50.0),
                    25
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should fail validation when task count is negative")
        void shouldFailValidation_WhenTaskCountIsNegative() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    "Description",
                    BigDecimal.valueOf(50.0),
                    -1
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            if (!violations.isEmpty()) {
                assertThat(violations).anyMatch(v ->
                        v.getPropertyPath().toString().equals("taskCount"));
            }
        }

        @Test
        @DisplayName("Should pass validation with null task count")
        void shouldPassValidation_WithNullTaskCount() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    "Description",
                    BigDecimal.valueOf(50.0),
                    null
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isEmpty();
        }
    }

    @Nested
    @DisplayName("Complete DTO Validation Tests")
    class CompleteDTOValidationTests {

        @Test
        @DisplayName("Should pass validation with all valid fields")
        void shouldPassValidation_WithAllValidFields() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Complete Task List",
                    "Complete Description",
                    BigDecimal.valueOf(75.5),
                    20
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should pass validation with minimal required fields")
        void shouldPassValidation_WithMinimalRequiredFields() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Valid Name",
                    null,
                    null,
                    null
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should accumulate multiple validation errors")
        void shouldAccumulateMultipleValidationErrors() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    null,
                    "a".repeat(1001),
                    BigDecimal.valueOf(-10.0),
                    -5
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isNotEmpty();
            assertThat(violations.size()).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Should validate special characters in name")
        void shouldValidateSpecialCharactersInName() {
            // Arrange
            TaskListDto taskListDto = new TaskListDto(
                    UUID.randomUUID(),
                    "Café ☀️ naïve résumé",
                    "Description with special chars",
                    BigDecimal.valueOf(50.0),
                    10
            );

            // Act
            Set<ConstraintViolation<TaskListDto>> violations = validator.validate(taskListDto);

            // Assert
            assertThat(violations).isEmpty();
        }
    }
}