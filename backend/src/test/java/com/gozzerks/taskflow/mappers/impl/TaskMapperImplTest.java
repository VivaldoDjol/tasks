package com.gozzerks.taskflow.mappers.impl;

import com.gozzerks.taskflow.domain.dto.CreateTaskDto;
import com.gozzerks.taskflow.domain.dto.TaskDto;
import com.gozzerks.taskflow.domain.dto.UpdateTaskDto;
import com.gozzerks.taskflow.domain.entities.Task;
import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.domain.enums.TaskPriority;
import com.gozzerks.taskflow.domain.enums.TaskStatus;
import com.gozzerks.taskflow.mappers.TaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskMapperImpl Tests")
class TaskMapperImplTest {

    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapperImpl();
    }

    @Nested
    @DisplayName("toDTO Tests")
    class ToDTOTests {

        @Test
        @DisplayName("Should map Entity to DTO with all fields")
        void shouldMapEntityToDTO_WithAllFields() {
            // Arrange
            UUID taskId = UUID.randomUUID();
            UUID taskListId = UUID.randomUUID();
            LocalDate dueDate = LocalDate.now().plusDays(7);

            TaskList taskList = new TaskList();
            taskList.setId(taskListId);
            taskList.setTitle("Parent Task List");

            Task task = new Task();
            task.setId(taskId);
            task.setTitle("Sample Task");
            task.setDescription("Sample Description");
            task.setDueDate(dueDate);
            task.setPriority(TaskPriority.HIGH);
            task.setStatus(TaskStatus.OPEN);
            task.setTaskList(taskList);

            // Act
            TaskDto taskDto = taskMapper.toDTO(task);

            // Assert
            assertThat(taskDto).isNotNull();
            assertThat(taskDto.id()).isEqualTo(taskId);
            assertThat(taskDto.title()).isEqualTo("Sample Task");
            assertThat(taskDto.description()).isEqualTo("Sample Description");
            assertThat(taskDto.dueDate()).isEqualTo(dueDate);
            assertThat(taskDto.priority()).isEqualTo(TaskPriority.HIGH);
            assertThat(taskDto.status()).isEqualTo(TaskStatus.OPEN);
            assertThat(taskDto.taskListId()).isEqualTo(taskListId);
        }

        @Test
        @DisplayName("Should handle Entity with null description")
        void shouldHandleNullDescription() {
            // Arrange
            Task task = new Task();
            task.setId(UUID.randomUUID());
            task.setTitle("Task Without Description");
            task.setDescription(null);
            task.setPriority(TaskPriority.MEDIUM);
            task.setStatus(TaskStatus.OPEN);

            TaskList taskList = new TaskList();
            taskList.setId(UUID.randomUUID());
            task.setTaskList(taskList);

            // Act
            TaskDto taskDto = taskMapper.toDTO(task);

            // Assert
            assertThat(taskDto).isNotNull();
            assertThat(taskDto.title()).isEqualTo("Task Without Description");
            assertThat(taskDto.description()).isNull();
        }

        @Test
        @DisplayName("Should handle Entity with null due date")
        void shouldHandleNullDueDate() {
            // Arrange
            Task task = new Task();
            task.setId(UUID.randomUUID());
            task.setTitle("Task Without Due Date");
            task.setDescription("Description");
            task.setDueDate(null);
            task.setPriority(TaskPriority.LOW);
            task.setStatus(TaskStatus.IN_PROGRESS);

            TaskList taskList = new TaskList();
            taskList.setId(UUID.randomUUID());
            task.setTaskList(taskList);

            // Act
            TaskDto taskDto = taskMapper.toDTO(task);

            // Assert
            assertThat(taskDto).isNotNull();
            assertThat(taskDto.dueDate()).isNull();
        }

        @Test
        @DisplayName("Should handle Entity with null TaskList")
        void shouldHandleNullTaskList() {
            // Arrange
            Task task = new Task();
            task.setId(UUID.randomUUID());
            task.setTitle("Orphan Task");
            task.setDescription("Task without list");
            task.setPriority(TaskPriority.MEDIUM);
            task.setStatus(TaskStatus.OPEN);
            task.setTaskList(null);

            // Act
            TaskDto taskDto = taskMapper.toDTO(task);

            // Assert
            assertThat(taskDto).isNotNull();
            assertThat(taskDto.taskListId()).isNull();
        }

        @Test
        @DisplayName("Should map different priority levels correctly")
        void shouldMapDifferentPriorityLevels() {
            // Arrange
            TaskList taskList = new TaskList();
            taskList.setId(UUID.randomUUID());

            Task lowPriorityTask = new Task();
            lowPriorityTask.setId(UUID.randomUUID());
            lowPriorityTask.setTitle("Low Priority");
            lowPriorityTask.setPriority(TaskPriority.LOW);
            lowPriorityTask.setStatus(TaskStatus.OPEN);
            lowPriorityTask.setTaskList(taskList);

            // Act
            TaskDto taskDto = taskMapper.toDTO(lowPriorityTask);

            // Assert
            assertThat(taskDto).isNotNull();
            assertThat(taskDto.priority()).isEqualTo(TaskPriority.LOW);
        }

        @Test
        @DisplayName("Should map different status values correctly")
        void shouldMapDifferentStatusValues() {
            // Arrange
            TaskList taskList = new TaskList();
            taskList.setId(UUID.randomUUID());

            Task closedTask = new Task();
            closedTask.setId(UUID.randomUUID());
            closedTask.setTitle("Closed Task");
            closedTask.setPriority(TaskPriority.MEDIUM);
            closedTask.setStatus(TaskStatus.CLOSED);
            closedTask.setTaskList(taskList);

            // Act
            TaskDto taskDto = taskMapper.toDTO(closedTask);

            // Assert
            assertThat(taskDto).isNotNull();
            assertThat(taskDto.status()).isEqualTo(TaskStatus.CLOSED);
        }
    }

    @Nested
    @DisplayName("fromDTO Tests")
    class FromDTOTests {

        @Test
        @DisplayName("Should map DTO to Entity with all fields")
        void shouldMapDTOToEntity_WithAllFields() {
            // Arrange
            UUID taskId = UUID.randomUUID();
            UUID taskListId = UUID.randomUUID();
            LocalDate dueDate = LocalDate.now().plusDays(5);

            TaskDto taskDto = new TaskDto(
                    taskId,
                    "DTO Task",
                    "DTO Description",
                    dueDate,
                    TaskPriority.HIGH,
                    TaskStatus.IN_PROGRESS,
                    taskListId
            );

            // Act
            Task task = taskMapper.fromDTO(taskDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getId()).isEqualTo(taskId);
            assertThat(task.getTitle()).isEqualTo("DTO Task");
            assertThat(task.getDescription()).isEqualTo("DTO Description");
            assertThat(task.getDueDate()).isEqualTo(dueDate);
            assertThat(task.getPriority()).isEqualTo(TaskPriority.HIGH);
            assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
            assertThat(task.getTaskList()).isNull();
        }

        @Test
        @DisplayName("Should handle DTO with null description")
        void shouldHandleNullDescription() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Task Title",
                    null,
                    LocalDate.now(),
                    TaskPriority.LOW,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Task task = taskMapper.fromDTO(taskDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getTitle()).isEqualTo("Task Title");
            assertThat(task.getDescription()).isNull();
        }

        @Test
        @DisplayName("Should handle DTO with null due date")
        void shouldHandleNullDueDate() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Task Title",
                    "Description",
                    null,
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Task task = taskMapper.fromDTO(taskDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getDueDate()).isNull();
        }

        @Test
        @DisplayName("Should handle DTO with null task list ID")
        void shouldHandleNullTaskListId() {
            // Arrange
            TaskDto taskDto = new TaskDto(
                    UUID.randomUUID(),
                    "Task Title",
                    "Description",
                    LocalDate.now(),
                    TaskPriority.HIGH,
                    TaskStatus.OPEN,
                    null
            );

            // Act
            Task task = taskMapper.fromDTO(taskDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getTaskList()).isNull();
        }

        @Test
        @DisplayName("Should map all priority levels correctly")
        void shouldMapAllPriorityLevels() {
            // Arrange
            TaskDto highPriorityDto = new TaskDto(
                    UUID.randomUUID(),
                    "High Priority",
                    "Description",
                    null,
                    TaskPriority.HIGH,
                    TaskStatus.OPEN,
                    UUID.randomUUID()
            );

            // Act
            Task task = taskMapper.fromDTO(highPriorityDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getPriority()).isEqualTo(TaskPriority.HIGH);
        }

        @Test
        @DisplayName("Should map all status values correctly")
        void shouldMapAllStatusValues() {
            // Arrange
            TaskDto inProgressDto = new TaskDto(
                    UUID.randomUUID(),
                    "In Progress Task",
                    "Description",
                    null,
                    TaskPriority.MEDIUM,
                    TaskStatus.IN_PROGRESS,
                    UUID.randomUUID()
            );

            // Act
            Task task = taskMapper.fromDTO(inProgressDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        }
    }

    @Nested
    @DisplayName("fromCreateDTO Tests")
    class FromCreateDTOTests {

        @Test
        @DisplayName("Should map CreateDTO to Entity with all fields")
        void shouldMapCreateDTOToEntity_WithAllFields() {
            // Arrange
            LocalDate dueDate = LocalDate.now().plusWeeks(2);

            CreateTaskDto createDto = new CreateTaskDto(
                    "New Task",
                    "New Task Description",
                    dueDate,
                    TaskPriority.HIGH,
                    TaskStatus.OPEN
            );

            // Act
            Task task = taskMapper.fromCreateDTO(createDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getId()).isNull();
            assertThat(task.getTitle()).isEqualTo("New Task");
            assertThat(task.getDescription()).isEqualTo("New Task Description");
            assertThat(task.getDueDate()).isEqualTo(dueDate);
            assertThat(task.getPriority()).isEqualTo(TaskPriority.HIGH);
            assertThat(task.getStatus()).isEqualTo(TaskStatus.OPEN);
            assertThat(task.getTaskList()).isNull(); // Set by service
        }

        @Test
        @DisplayName("Should handle CreateDTO with null description")
        void shouldHandleNullDescription() {
            // Arrange
            CreateTaskDto createDto = new CreateTaskDto(
                    "Task Without Description",
                    null,
                    LocalDate.now(),
                    TaskPriority.LOW,
                    TaskStatus.OPEN
            );

            // Act
            Task task = taskMapper.fromCreateDTO(createDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getTitle()).isEqualTo("Task Without Description");
            assertThat(task.getDescription()).isNull();
        }

        @Test
        @DisplayName("Should handle CreateDTO with null due date")
        void shouldHandleNullDueDate() {
            // Arrange
            CreateTaskDto createDto = new CreateTaskDto(
                    "Task Without Due Date",
                    "Description",
                    null,
                    TaskPriority.MEDIUM,
                    TaskStatus.OPEN
            );

            // Act
            Task task = taskMapper.fromCreateDTO(createDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getDueDate()).isNull();
        }

        @Test
        @DisplayName("Should default to OPEN status if not provided")
        void shouldDefaultToOpenStatus() {
            // Arrange
            CreateTaskDto createDto = new CreateTaskDto(
                    "New Task",
                    "Description",
                    null,
                    TaskPriority.MEDIUM,
                    null
            );

            // Act
            Task task = taskMapper.fromCreateDTO(createDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getStatus()).isIn(TaskStatus.OPEN, null);
        }

        @Test
        @DisplayName("Should default to MEDIUM priority if not provided")
        void shouldDefaultToMediumPriority() {
            // Arrange
            CreateTaskDto createDto = new CreateTaskDto(
                    "New Task",
                    "Description",
                    null,
                    null,
                    TaskStatus.OPEN
            );

            // Act
            Task task = taskMapper.fromCreateDTO(createDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getPriority()).isIn(TaskPriority.MEDIUM, null);
        }

        @Test
        @DisplayName("Should map with minimum required fields")
        void shouldMapWithMinimumRequiredFields() {
            // Arrange
            CreateTaskDto createDto = new CreateTaskDto(
                    "Minimal Task",
                    null,
                    null,
                    null,
                    null
            );

            // Act
            Task task = taskMapper.fromCreateDTO(createDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getTitle()).isEqualTo("Minimal Task");
        }
    }

    @Nested
    @DisplayName("fromUpdateDTO Tests")
    class FromUpdateDTOTests {

        @Test
        @DisplayName("Should map UpdateDTO to Entity with all fields")
        void shouldMapUpdateDTOToEntity_WithAllFields() {
            // Arrange
            LocalDate updatedDueDate = LocalDate.now().plusDays(10);

            UpdateTaskDto updateDto = new UpdateTaskDto(
                    "Updated Task Title",
                    "Updated Description",
                    updatedDueDate,
                    TaskPriority.LOW,
                    TaskStatus.CLOSED
            );

            // Act
            Task task = taskMapper.fromUpdateDTO(updateDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getTitle()).isEqualTo("Updated Task Title");
            assertThat(task.getDescription()).isEqualTo("Updated Description");
            assertThat(task.getDueDate()).isEqualTo(updatedDueDate);
            assertThat(task.getPriority()).isEqualTo(TaskPriority.LOW);
            assertThat(task.getStatus()).isEqualTo(TaskStatus.CLOSED);
        }

        @Test
        @DisplayName("Should handle UpdateDTO with null fields for partial update")
        void shouldHandleNullFieldsForPartialUpdate() {
            // Arrange
            UpdateTaskDto updateDto = new UpdateTaskDto(
                    "Updated Title Only",
                    null,
                    null,
                    null,
                    null
            );

            // Act
            Task task = taskMapper.fromUpdateDTO(updateDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getTitle()).isEqualTo("Updated Title Only");
            assertThat(task.getDescription()).isNull();
            assertThat(task.getDueDate()).isNull();
            assertThat(task.getPriority()).isNull();
            assertThat(task.getStatus()).isNull();
        }

        @Test
        @DisplayName("Should handle priority update only")
        void shouldHandlePriorityUpdateOnly() {
            // Arrange
            UpdateTaskDto updateDto = new UpdateTaskDto(
                    null,
                    null,
                    null,
                    TaskPriority.HIGH,
                    null
            );

            // Act
            Task task = taskMapper.fromUpdateDTO(updateDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getPriority()).isEqualTo(TaskPriority.HIGH);
        }

        @Test
        @DisplayName("Should handle status update only")
        void shouldHandleStatusUpdateOnly() {
            // Arrange
            UpdateTaskDto updateDto = new UpdateTaskDto(
                    null,
                    null,
                    null,
                    null,
                    TaskStatus.IN_PROGRESS
            );

            // Act
            Task task = taskMapper.fromUpdateDTO(updateDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        }

        @Test
        @DisplayName("Should handle due date update only")
        void shouldHandleDueDateUpdateOnly() {
            // Arrange
            LocalDate newDueDate = LocalDate.now().plusMonths(1);

            UpdateTaskDto updateDto = new UpdateTaskDto(
                    null,
                    null,
                    newDueDate,
                    null,
                    null
            );

            // Act
            Task task = taskMapper.fromUpdateDTO(updateDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getDueDate()).isEqualTo(newDueDate);
        }

        @Test
        @DisplayName("Should handle description update only")
        void shouldHandleDescriptionUpdateOnly() {
            // Arrange
            UpdateTaskDto updateDto = new UpdateTaskDto(
                    null,
                    "Updated description content",
                    null,
                    null,
                    null
            );

            // Act
            Task task = taskMapper.fromUpdateDTO(updateDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getDescription()).isEqualTo("Updated description content");
        }

        @Test
        @DisplayName("Should allow clearing description with empty string")
        void shouldAllowClearingDescription() {
            // Arrange
            UpdateTaskDto updateDto = new UpdateTaskDto(
                    null,
                    "",
                    null,
                    null,
                    null
            );

            // Act
            Task task = taskMapper.fromUpdateDTO(updateDto);

            // Assert
            assertThat(task).isNotNull();
            assertThat(task.getDescription()).isEmpty();
        }
    }
}