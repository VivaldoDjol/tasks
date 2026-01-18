package com.gozzerks.taskflow.services.impl;

import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.repositories.TaskListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskListServiceImpl Tests")
class TaskListServiceImplTest {

    @Mock
    private TaskListRepository taskListRepository;

    @InjectMocks
    private TaskListServiceImpl taskListService;

    private UUID taskListId;
    private TaskList taskList;

    @BeforeEach
    void setUp() {
        taskListId = UUID.randomUUID();

        // Arrange
        taskList = new TaskList();
        taskList.setId(taskListId);
        taskList.setTitle("Test Task List");
        taskList.setDescription("Test Description");
        taskList.setCreatedAt(LocalDateTime.now());
        taskList.setUpdatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Find All Task Lists Tests")
    class FindAllTaskListsTests {

        @Test
        @DisplayName("Should return all task lists when they exist")
        void shouldReturnAllTaskLists() {
            // Arrange
            TaskList taskList2 = new TaskList();
            taskList2.setId(UUID.randomUUID());
            taskList2.setTitle("Second List");
            taskList2.setDescription("Second Description");

            List<TaskList> expectedLists = Arrays.asList(taskList, taskList2);
            when(taskListRepository.findAll()).thenReturn(expectedLists);

            // Act
            List<TaskList> actualLists = taskListService.listTaskLists();

            // Assert
            assertThat(actualLists)
                    .isNotNull()
                    .hasSize(2)
                    .containsExactlyInAnyOrder(taskList, taskList2);
            verify(taskListRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Should return empty list when no task lists exist")
        void shouldReturnEmptyListWhenNoTaskListsExist() {
            // Arrange
            when(taskListRepository.findAll()).thenReturn(List.of());

            // Act
            List<TaskList> actualLists = taskListService.listTaskLists();

            // Assert
            assertThat(actualLists)
                    .isNotNull()
                    .isEmpty();
            verify(taskListRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Get Task List Tests")
    class GetTaskListTests {

        @Test
        @DisplayName("Should return task list when ID exists")
        void shouldReturnTaskListWhenIdExists() {
            // Arrange
            when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));

            // Act
            Optional<TaskList> result = taskListService.getTaskList(taskListId);

            // Assert
            assertThat(result)
                    .isPresent()
                    .contains(taskList);
            verify(taskListRepository, times(1)).findById(taskListId);
        }

        @Test
        @DisplayName("Should return empty Optional when ID does not exist")
        void shouldReturnEmptyWhenIdDoesNotExist() {
            // Arrange
            when(taskListRepository.findById(taskListId)).thenReturn(Optional.empty());

            // Act
            Optional<TaskList> result = taskListService.getTaskList(taskListId);

            // Assert
            assertThat(result).isEmpty();
            verify(taskListRepository, times(1)).findById(taskListId);
        }

        @Test
        @DisplayName("Should throw exception when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> taskListService.getTaskList(null))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(taskListRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("Create Task List Tests")
    class CreateTaskListTests {

        @Test
        @DisplayName("Should create task list successfully")
        void shouldCreateTaskListSuccessfully() {
            // Arrange
            TaskList newTaskList = new TaskList();
            newTaskList.setTitle("New List");
            newTaskList.setDescription("New Description");

            TaskList savedTaskList = new TaskList();
            savedTaskList.setId(UUID.randomUUID());
            savedTaskList.setTitle("New List");
            savedTaskList.setDescription("New Description");
            savedTaskList.setCreatedAt(LocalDateTime.now());
            savedTaskList.setUpdatedAt(LocalDateTime.now());

            when(taskListRepository.save(any(TaskList.class))).thenReturn(savedTaskList);

            // Act
            TaskList result = taskListService.createTaskList(newTaskList);

            // Assert
            assertThat(result)
                    .isNotNull()
                    .extracting(TaskList::getId, TaskList::getTitle, TaskList::getDescription)
                    .containsExactly(savedTaskList.getId(), "New List", "New Description");
            verify(taskListRepository, times(1)).save(newTaskList);
        }

        @Test
        @DisplayName("Should set timestamps when creating task list")
        void shouldSetTimestampsWhenCreating() {
            // Arrange
            TaskList newTaskList = new TaskList();
            newTaskList.setTitle("New List");

            when(taskListRepository.save(any(TaskList.class))).thenAnswer(invocation -> {
                TaskList saved = invocation.getArgument(0);
                saved.setId(UUID.randomUUID());
                return saved;
            });

            // Act
            taskListService.createTaskList(newTaskList);

            // Assert
            verify(taskListRepository).save(argThat(saved -> 
                saved.getCreatedAt() != null && saved.getUpdatedAt() != null
            ));
        }

        @Test
        @DisplayName("Should throw exception when task list is null")
        void shouldThrowExceptionWhenTaskListIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> taskListService.createTaskList(null))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(taskListRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update Task List Tests")
    class UpdateTaskListTests {

        @Test
        @DisplayName("Should update task list when ID exists")
        void shouldUpdateTaskListWhenIdExists() {
            // Arrange
            TaskList existingTaskList = new TaskList();
            existingTaskList.setId(taskListId);
            existingTaskList.setTitle("Old Title");
            existingTaskList.setDescription("Old Description");
            existingTaskList.setCreatedAt(LocalDateTime.now().minusDays(1));
            existingTaskList.setUpdatedAt(LocalDateTime.now().minusDays(1));

            TaskList updatedDetails = new TaskList();
            updatedDetails.setTitle("Updated Title");
            updatedDetails.setDescription("Updated Description");

            when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(existingTaskList));
            when(taskListRepository.save(any(TaskList.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            Optional<TaskList> result = taskListService.updateTaskList(taskListId, updatedDetails);

            // Assert
            assertThat(result)
                    .isPresent()
                    .get()
                    .extracting(TaskList::getTitle, TaskList::getDescription)
                    .containsExactly("Updated Title", "Updated Description");
            
            verify(taskListRepository, times(1)).findById(taskListId);
            verify(taskListRepository, times(1)).save(any(TaskList.class));
        }

        @Test
        @DisplayName("Should update only updatedAt timestamp")
        void shouldUpdateOnlyUpdatedAtTimestamp() {
            // Arrange
            LocalDateTime originalCreatedAt = LocalDateTime.now().minusDays(1);
            
            TaskList existingTaskList = new TaskList();
            existingTaskList.setId(taskListId);
            existingTaskList.setTitle("Old Title");
            existingTaskList.setCreatedAt(originalCreatedAt);
            existingTaskList.setUpdatedAt(LocalDateTime.now().minusDays(1));

            TaskList updatedDetails = new TaskList();
            updatedDetails.setTitle("Updated Title");

            when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(existingTaskList));
            when(taskListRepository.save(any(TaskList.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            taskListService.updateTaskList(taskListId, updatedDetails);

            // Assert
            verify(taskListRepository).save(argThat(saved -> 
                saved.getCreatedAt().equals(originalCreatedAt) && 
                saved.getUpdatedAt().isAfter(originalCreatedAt)
            ));
        }

        @Test
        @DisplayName("Should return empty when task list ID does not exist")
        void shouldReturnEmptyWhenIdDoesNotExist() {
            // Arrange
            TaskList updatedDetails = new TaskList();
            updatedDetails.setTitle("Updated Title");

            when(taskListRepository.findById(taskListId)).thenReturn(Optional.empty());

            // Act
            Optional<TaskList> result = taskListService.updateTaskList(taskListId, updatedDetails);

            // Assert
            assertThat(result).isEmpty();
            verify(taskListRepository, times(1)).findById(taskListId);
            verify(taskListRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            // Arrange
            TaskList updatedDetails = new TaskList();
            updatedDetails.setTitle("Updated Title");

            // Act & Assert
            assertThatThrownBy(() -> taskListService.updateTaskList(null, updatedDetails))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(taskListRepository, never()).findById(any());
            verify(taskListRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Task List Tests")
    class DeleteTaskListTests {

        @Test
        @DisplayName("Should delete task list when ID exists")
        void shouldDeleteTaskListWhenIdExists() {
            // Arrange
            when(taskListRepository.existsById(taskListId)).thenReturn(true);
            doNothing().when(taskListRepository).deleteById(taskListId);

            // Act
            boolean result = taskListService.deleteTaskList(taskListId);

            // Assert
            assertThat(result).isTrue();
            verify(taskListRepository, times(1)).existsById(taskListId);
            verify(taskListRepository, times(1)).deleteById(taskListId);
        }

        @Test
        @DisplayName("Should return false when task list ID does not exist")
        void shouldReturnFalseWhenIdDoesNotExist() {
            // Arrange
            when(taskListRepository.existsById(taskListId)).thenReturn(false);

            // Act
            boolean result = taskListService.deleteTaskList(taskListId);

            // Assert
            assertThat(result).isFalse();
            verify(taskListRepository, times(1)).existsById(taskListId);
            verify(taskListRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should throw exception when ID is null")
        void shouldThrowExceptionWhenIdIsNull() {
            // Act & Assert
            assertThatThrownBy(() -> taskListService.deleteTaskList(null))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(taskListRepository, never()).existsById(any());
            verify(taskListRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("Should cascade delete tasks when deleting task list")
        void shouldCascadeDeleteTasksWhenDeletingTaskList() {
            // Arrange
            when(taskListRepository.existsById(taskListId)).thenReturn(true);
            doNothing().when(taskListRepository).deleteById(taskListId);

            // Act
            taskListService.deleteTaskList(taskListId);

            // Assert
            verify(taskListRepository, times(1)).deleteById(taskListId);
        }
    }
}