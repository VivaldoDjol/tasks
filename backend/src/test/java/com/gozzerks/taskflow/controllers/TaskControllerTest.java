package com.gozzerks.taskflow.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gozzerks.taskflow.domain.dto.CreateTaskDto;
import com.gozzerks.taskflow.domain.dto.TaskDto;
import com.gozzerks.taskflow.domain.dto.UpdateTaskDto;
import com.gozzerks.taskflow.domain.entities.Task;
import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.domain.enums.TaskPriority;
import com.gozzerks.taskflow.domain.enums.TaskStatus;
import com.gozzerks.taskflow.mappers.TaskMapper;
import com.gozzerks.taskflow.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autocomplete.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@DisplayName("TaskController Tests")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    private UUID taskListId;
    private UUID taskId;
    private Task task;
    private TaskDto taskDto;
    private CreateTaskDto createDto;
    private UpdateTaskDto updateDto;

    @BeforeEach
    void setUp() {
        taskListId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        // Arrange
        TaskList taskList = new TaskList();
        taskList.setId(taskListId);
        taskList.setTitle("Test Task List");

        // Arrange
        task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setPriority(TaskPriority.HIGH);
        task.setStatus(TaskStatus.OPEN);
        task.setTaskList(taskList);
        task.setDueDate(LocalDate.now().plusDays(7));
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        // Arrange
        taskDto = new TaskDto();
        taskDto.setId(taskId);
        taskDto.setTitle("Test Task");
        taskDto.setDescription("Test Description");
        taskDto.setPriority(TaskPriority.HIGH);
        taskDto.setStatus(TaskStatus.OPEN);
        taskDto.setTaskListId(taskListId);
        taskDto.setDueDate(LocalDate.now().plusDays(7));

        // Arrange
        createDto = new CreateTaskDto();
        createDto.setTitle("New Task");
        createDto.setDescription("New Description");
        createDto.setPriority(TaskPriority.MEDIUM);
        createDto.setStatus(TaskStatus.OPEN);

        updateDto = new UpdateTaskDto();
        updateDto.setTitle("Updated Task");
        updateDto.setDescription("Updated Description");
        updateDto.setPriority(TaskPriority.LOW);
        updateDto.setStatus(TaskStatus.CLOSED);
    }

    @Test
    @DisplayName("GET /task-lists/{id}/tasks - Should return all tasks in task list")
    void listTasks_ShouldReturnAllTasks() throws Exception {
        // Arrange
        Task task2 = new Task();
        task2.setId(UUID.randomUUID());
        task2.setTitle("Second Task");
        task2.setDescription("Second Description");
        task2.setPriority(TaskPriority.LOW);
        task2.setStatus(TaskStatus.OPEN);

        TaskDto taskDto2 = new TaskDto();
        taskDto2.setId(task2.getId());
        taskDto2.setTitle("Second Task");
        taskDto2.setDescription("Second Description");
        taskDto2.setPriority(TaskPriority.LOW);
        taskDto2.setStatus(TaskStatus.OPEN);

        List<Task> tasks = Arrays.asList(task, task2);

        when(taskService.listTasks(taskListId)).thenReturn(tasks);
        when(taskMapper.toDTO(task)).thenReturn(taskDto);
        when(taskMapper.toDTO(task2)).thenReturn(taskDto2);

        // Act & Assert
        mockMvc.perform(get("/task-lists/{taskListId}/tasks", taskListId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(taskId.toString())))
                .andExpect(jsonPath("$[0].title", is("Test Task")))
                .andExpect(jsonPath("$[0].priority", is("HIGH")))
                .andExpect(jsonPath("$[0].status", is("OPEN")))
                .andExpect(jsonPath("$[1].title", is("Second Task")));

        verify(taskService, times(1)).listTasks(taskListId);
        verify(taskMapper, times(2)).toDTO(any(Task.class));
    }

    @Test
    @DisplayName("GET /task-lists/{id}/tasks - Should return empty list when no tasks exist")
    void listTasks_ShouldReturnEmptyList_WhenNoTasksExist() throws Exception {
        // Arrange
        when(taskService.listTasks(taskListId)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/task-lists/{taskListId}/tasks", taskListId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(taskService, times(1)).listTasks(taskListId);
        verify(taskMapper, never()).toDTO(any(Task.class));
    }

    @Test
    @DisplayName("GET /task-lists/{listId}/tasks/{taskId} - Should return task when both IDs exist")
    void getTask_ShouldReturnTask_WhenBothIdsExist() throws Exception {
        // Arrange
        when(taskService.getTask(taskListId, taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toDTO(task)).thenReturn(taskDto);

        // Act & Assert
        mockMvc.perform(get("/task-lists/{taskListId}/tasks/{taskId}", taskListId, taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskId.toString())))
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.priority", is("HIGH")))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.taskListId", is(taskListId.toString())));

        verify(taskService, times(1)).getTask(taskListId, taskId);
        verify(taskMapper, times(1)).toDTO(task);
    }

    @Test
    @DisplayName("GET /task-lists/{listId}/tasks/{taskId} - Should return 404 when task not found")
    void getTask_ShouldReturnNotFound_WhenTaskDoesNotExist() throws Exception {
        // Arrange
        UUID nonExistentTaskId = UUID.randomUUID();
        when(taskService.getTask(taskListId, nonExistentTaskId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/task-lists/{taskListId}/tasks/{taskId}", taskListId, nonExistentTaskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTask(taskListId, nonExistentTaskId);
        verify(taskMapper, never()).toDTO(any(Task.class));
    }

    @Test
    @DisplayName("POST /task-lists/{id}/tasks - Should create task with valid data")
    void createTask_ShouldCreateTask_WithValidData() throws Exception {
        // Arrange
        when(taskMapper.fromCreateDTO(any(CreateTaskDto.class))).thenReturn(task);
        when(taskService.createTask(eq(taskListId), any(Task.class))).thenReturn(task);
        when(taskMapper.toDTO(task)).thenReturn(taskDto);

        // Act & Assert
        mockMvc.perform(post("/task-lists/{taskListId}/tasks", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(taskId.toString())))
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.priority", is("HIGH")))
                .andExpect(jsonPath("$.status", is("OPEN")));

        verify(taskMapper, times(1)).fromCreateDTO(any(CreateTaskDto.class));
        verify(taskService, times(1)).createTask(eq(taskListId), any(Task.class));
        verify(taskMapper, times(1)).toDTO(task);
    }

    @Test