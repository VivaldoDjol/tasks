package com.gozzerks.taskflow.services.impl;

import com.gozzerks.taskflow.entities.Task;
import com.gozzerks.taskflow.entities.TaskList;
import com.gozzerks.taskflow.repositories.TaskListRepository;
import com.gozzerks.taskflow.repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskServiceImpl Tests")
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskListRepository taskListRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private TaskList taskList;
    private Long taskId;
    private Long taskListId;

    @BeforeEach
    void setUp() {
        taskId = 1L;
        taskListId = 1L;

        taskList = new TaskList();
        taskList.setId(taskListId);
        taskList.setName("Test Task List");

        task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setCompleted(false);
        task.setTaskList(taskList);
    }

    @Nested
    @DisplayName("Create Task")
    class CreateTaskTests {

        @Test
        @DisplayName("Should create task successfully with valid task list")
        void shouldCreateTaskSuccessfully() {
            // Arrange
            when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));
            when(taskRepository.save(any(Task.class))).thenReturn(task);

            // Act
            Task result = taskService.createTask(taskListId, task);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Test Task");
            assertThat(result.getTaskList()).isEqualTo(taskList);
            verify(taskListRepository).findById(taskListId);
            verify(taskRepository).save(task);
        }

        @Test
        @DisplayName("Should set task list relationship when creating task")
        void shouldSetTaskListRelationship() {
            // Arrange
            ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
            when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));
            when(taskRepository.save(any(Task.class))).thenReturn(task);

            // Act
            taskService.createTask(taskListId, task);

            // Assert
            verify(taskRepository).save(taskCaptor.capture());
            Task capturedTask = taskCaptor.getValue();
            assertThat(capturedTask.getTaskList()).isEqualTo(taskList);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when task list not found")
        void shouldThrowExceptionWhenTaskListNotFound() {
            // Arrange
            when(taskListRepository.findById(taskListId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> taskService.createTask(taskListId, task))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("TaskList not found with id: " + taskListId);

            verify(taskListRepository).findById(taskListId);
            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when task is null")
        void shouldThrowExceptionWhenTaskIsNull() {
            // Arrange
            when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));

            // Act & Assert
            assertThatThrownBy(() -> taskService.createTask(taskListId, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Task cannot be null");

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle repository exceptions during save")
        void shouldHandleRepositoryExceptions() {
            // Arrange
            when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));
            when(taskRepository.save(any(Task.class)))
                    .thenThrow(new DataAccessException("Database error") {});

            // Act & Assert
            assertThatThrownBy(() -> taskService.createTask(taskListId, task))
                    .isInstanceOf(DataAccessException.class)
                    .hasMessageContaining("Database error");

            verify(taskRepository).save(any(Task.class));
        }
    }

    @Nested
    @DisplayName("Retrieve Tasks")
    class RetrieveTasksTests {

        @Test
        @DisplayName("Should retrieve all tasks for a task list")
        void shouldRetrieveAllTasksForTaskList() {
            // Arrange
            Task task2 = new Task();
            task2.setId(2L);
            task2.setTitle("Second Task");
            task2.setTaskList(taskList);

            List<Task> tasks = List.of(task, task2);
            when(taskRepository.findByTaskListId(taskListId)).thenReturn(tasks);

            // Act
            List<Task> result = taskService.getTasksByTaskListId(taskListId);

            // Assert
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(task, task2);
            verify(taskRepository).findByTaskListId(taskListId);
        }

        @Test
        @DisplayName("Should return empty list when no tasks exist for task list")
        void shouldReturnEmptyListWhenNoTasksExist() {
            // Arrange
            when(taskRepository.findByTaskListId(taskListId)).thenReturn(List.of());

            // Act
            List<Task> result = taskService.getTasksByTaskListId(taskListId);

            // Assert
            assertThat(result).isEmpty();
            verify(taskRepository).findByTaskListId(taskListId);
        }

        @Test
        @DisplayName("Should retrieve task by ID successfully")
        void shouldRetrieveTaskById() {
            // Arrange
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

            // Act
            Optional<Task> result = taskService.getTaskById(taskId);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(task);
            assertThat(result.get().getTitle()).isEqualTo("Test Task");
            verify(taskRepository).findById(taskId);
        }

        @Test
        @DisplayName("Should return empty optional when task not found by ID")
        void shouldReturnEmptyWhenTaskNotFound() {
            // Arrange
            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            // Act
            Optional<Task> result = taskService.getTaskById(taskId);

            // Assert
            assertThat(result).isEmpty();
            verify(taskRepository).findById(taskId);
        }

        @Test
        @DisplayName("Should handle null task list ID gracefully")
        void shouldHandleNullTaskListId() {
            // Arrange
            when(taskRepository.findByTaskListId(null)).thenReturn(List.of());

            // Act
            List<Task> result = taskService.getTasksByTaskListId(null);

            // Assert
            assertThat(result).isEmpty();
            verify(taskRepository).findByTaskListId(null);
        }
    }

    @Nested
    @DisplayName("Update Task")
    class UpdateTaskTests {

        @Test
        @DisplayName("Should update task with new values")
        void shouldUpdateTaskWithNewValues() {
            // Arrange
            Task updates = new Task();
            updates.setTitle("Updated Title");
            updates.setDescription("Updated Description");
            updates.setCompleted(true);

            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Optional<Task> result = taskService.updateTask(taskId, updates);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getTitle()).isEqualTo("Updated Title");
            assertThat(result.get().getDescription()).isEqualTo("Updated Description");
            assertThat(result.get().isCompleted()).isTrue();
            verify(taskRepository).findById(taskId);
            verify(taskRepository).save(any(Task.class));
        }

        @Test
        @DisplayName("Should preserve task list relationship during update")
        void shouldPreserveTaskListRelationship() {
            // Arrange
            Task updates = new Task();
            updates.setTitle("Updated Title");

            ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            taskService.updateTask(taskId, updates);

            // Assert
            verify(taskRepository).save(taskCaptor.capture());
            Task savedTask = taskCaptor.getValue();
            assertThat(savedTask.getTaskList()).isEqualTo(taskList);
        }

        @Test
        @DisplayName("Should update only provided fields")
        void shouldUpdateOnlyProvidedFields() {
            // Arrange
            Task updates = new Task();
            updates.setTitle("New Title Only");

            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Optional<Task> result = taskService.updateTask(taskId, updates);

            // Assert
            assertThat(result).isPresent();
            assertThat(result.get().getTitle()).isEqualTo("New Title Only");
            assertThat(result.get().getDescription()).isEqualTo("Test Description");
            assertThat(result.get().isCompleted()).isFalse();
        }

        @Test
        @DisplayName("Should return empty optional when updating non-existent task")
        void shouldReturnEmptyWhenTaskNotFound() {
            // Arrange
            Task updates = new Task();
            updates.setTitle("Updated");

            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            // Act
            Optional<Task> result = taskService.updateTask(taskId, updates);

            // Assert
            assertThat(result).isEmpty();
            verify(taskRepository).findById(taskId);
            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when updates are null")
        void shouldThrowExceptionWhenUpdatesAreNull() {
            // Arrange
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

            // Act & Assert
            assertThatThrownBy(() -> taskService.updateTask(taskId, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Task updates cannot be null");

            verify(taskRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle repository exceptions during update")
        void shouldHandleRepositoryExceptionsDuringUpdate() {
            // Arrange
            Task updates = new Task();
            updates.setTitle("Updated");

            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(taskRepository.save(any(Task.class)))
                    .thenThrow(new DataAccessException("Update failed") {});

            // Act & Assert
            assertThatThrownBy(() -> taskService.updateTask(taskId, updates))
                    .isInstanceOf(DataAccessException.class);

            verify(taskRepository).save(any(Task.class));
        }
    }

    @Nested
    @DisplayName("Delete Task")
    class DeleteTaskTests {

        @Test
        @DisplayName("Should delete existing task successfully")
        void shouldDeleteTaskSuccessfully() {
            // Arrange
            when(taskRepository.existsById(taskId)).thenReturn(true);
            doNothing().when(taskRepository).deleteById(taskId);

            // Act
            boolean result = taskService.deleteTask(taskId);

            // Assert
            assertThat(result).isTrue();
            verify(taskRepository).existsById(taskId);
            verify(taskRepository).deleteById(taskId);
        }

        @Test
        @DisplayName("Should return false when deleting non-existent task")
        void shouldReturnFalseWhenTaskNotFound() {
            // Arrange
            when(taskRepository.existsById(taskId)).thenReturn(false);

            // Act
            boolean result = taskService.deleteTask(taskId);

            // Assert
            assertThat(result).isFalse();
            verify(taskRepository).existsById(taskId);
            verify(taskRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should invoke deleteById exactly once for existing task")
        void shouldInvokeDeleteByIdOnce() {
            // Arrange
            when(taskRepository.existsById(taskId)).thenReturn(true);

            // Act
            taskService.deleteTask(taskId);

            // Assert
            verify(taskRepository, times(1)).deleteById(taskId);
        }

        @Test
        @DisplayName("Should handle repository exceptions during delete")
        void shouldHandleRepositoryExceptionsDuringDelete() {
            // Arrange
            when(taskRepository.existsById(taskId)).thenReturn(true);
            doThrow(new DataAccessException("Delete failed") {})
                    .when(taskRepository).deleteById(taskId);

            // Act & Assert
            assertThatThrownBy(() -> taskService.deleteTask(taskId))
                    .isInstanceOf(DataAccessException.class)
                    .hasMessageContaining("Delete failed");

            verify(taskRepository).deleteById(taskId);
        }

        @Test
        @DisplayName("Should handle null task ID")
        void shouldHandleNullTaskId() {
            // Arrange
            when(taskRepository.existsById(null)).thenReturn(false);

            // Act
            boolean result = taskService.deleteTask(null);

            // Assert
            assertThat(result).isFalse();
            verify(taskRepository).existsById(null);
            verify(taskRepository, never()).deleteById(any());
        }
    }
}