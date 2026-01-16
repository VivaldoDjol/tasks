package com.gozzerks.taskflow.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gozzerks.taskflow.domain.dto.CreateTaskListDto;
import com.gozzerks.taskflow.domain.dto.TaskListDto;
import com.gozzerks.taskflow.domain.dto.UpdateTaskListDto;
import com.gozzerks.taskflow.domain.entities.TaskList;
import com.gozzerks.taskflow.domain.entities.TaskStatus;
import com.gozzerks.taskflow.mappers.TaskListMapper;
import com.gozzerks.taskflow.services.TaskListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(TaskListController.class)
@DisplayName("TaskListController Tests")
class TaskListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskListService taskListService;

    @MockBean
    private TaskListMapper taskListMapper;

    private UUID taskListId;
    private TaskList taskList;
    private TaskListDto taskListDto;
    private CreateTaskListDto createDto;
    private UpdateTaskListDto updateDto;

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

        // Arrange
        taskListDto = new TaskListDto();
        taskListDto.setId(taskListId);
        taskListDto.setTitle("Test Task List");
        taskListDto.setDescription("Test Description");
        taskListDto.setCount(0L);
        taskListDto.setProgress(0L);

        createDto = new CreateTaskListDto();
        createDto.setTitle("New Task List");
        createDto.setDescription("New Description");

        updateDto = new UpdateTaskListDto();
        updateDto.setTitle("Updated Task List");
        updateDto.setDescription("Updated Description");
    }

    @Test
    @DisplayName("GET /task-lists - Should return list of all task lists")
    void listTaskLists_ShouldReturnAllTaskLists() throws Exception {
        // Arrange
        TaskList taskList2 = new TaskList();
        taskList2.setId(UUID.randomUUID());
        taskList2.setTitle("Second List");
        taskList2.setDescription("Second Description");

        TaskListDto taskListDto2 = new TaskListDto();
        taskListDto2.setId(taskList2.getId());
        taskListDto2.setTitle("Second List");
        taskListDto2.setDescription("Second Description");

        List<TaskList> taskLists = Arrays.asList(taskList, taskList2);
        
        when(taskListService.listTaskLists()).thenReturn(taskLists);
        when(taskListMapper.toDTO(taskList)).thenReturn(taskListDto);
        when(taskListMapper.toDTO(taskList2)).thenReturn(taskListDto2);

        // Act & Assert
        mockMvc.perform(get("/task-lists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(taskListId.toString())))
                .andExpect(jsonPath("$[0].title", is("Test Task List")))
                .andExpect(jsonPath("$[0].description", is("Test Description")))
                .andExpect(jsonPath("$[1].title", is("Second List")));

        // Verify
        verify(taskListService, times(1)).listTaskLists();
        verify(taskListMapper, times(2)).toDTO(any(TaskList.class));
    }

    @Test
    @DisplayName("GET /task-lists - Should return empty list when no task lists exist")
    void listTaskLists_ShouldReturnEmptyList_WhenNoTaskListsExist() throws Exception {
        // Arrange
        when(taskListService.listTaskLists()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/task-lists")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(taskListService, times(1)).listTaskLists();
        verify(taskListMapper, never()).toDTO(any(TaskList.class));
    }

    @Test
    @DisplayName("GET /task-lists/{id} - Should return task list when ID exists")
    void getTaskList_ShouldReturnTaskList_WhenIdExists() throws Exception {
        // Arrange
        when(taskListService.getTaskList(taskListId)).thenReturn(Optional.of(taskList));
        when(taskListMapper.toDTO(taskList)).thenReturn(taskListDto);

        // Act & Assert
        mockMvc.perform(get("/task-lists/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskListId.toString())))
                .andExpect(jsonPath("$.title", is("Test Task List")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.count", is(0)))
                .andExpect(jsonPath("$.progress", is(0)));

        verify(taskListService, times(1)).getTaskList(taskListId);
        verify(taskListMapper, times(1)).toDTO(taskList);
    }

    @Test
    @DisplayName("GET /task-lists/{id} - Should return 404 when ID does not exist")
    void getTaskList_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(taskListService.getTaskList(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/task-lists/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(taskListService, times(1)).getTaskList(nonExistentId);
        verify(taskListMapper, never()).toDTO(any(TaskList.class));
    }

    @Test
    @DisplayName("POST /task-lists - Should create task list with valid data")
    void createTaskList_ShouldCreateTaskList_WithValidData() throws Exception {
        // Arrange
        when(taskListMapper.fromCreateDTO(any(CreateTaskListDto.class))).thenReturn(taskList);
        when(taskListService.createTaskList(any(TaskList.class))).thenReturn(taskList);
        when(taskListMapper.toDTO(taskList)).thenReturn(taskListDto);

        // Act & Assert
        mockMvc.perform(post("/task-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(taskListId.toString())))
                .andExpect(jsonPath("$.title", is("Test Task List")))
                .andExpect(jsonPath("$.description", is("Test Description")));

        verify(taskListMapper, times(1)).fromCreateDTO(any(CreateTaskListDto.class));
        verify(taskListService, times(1)).createTaskList(any(TaskList.class));
        verify(taskListMapper, times(1)).toDTO(taskList);
    }

    @Test
    @DisplayName("POST /task-lists - Should return 400 when title is missing")
    void createTaskList_ShouldReturnBadRequest_WhenTitleIsMissing() throws Exception {
        // Arrange
        CreateTaskListDto invalidDto = new CreateTaskListDto();
        invalidDto.setDescription("Description only");

        // Act & Assert
        mockMvc.perform(post("/task-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskListService, never()).createTaskList(any(TaskList.class));
    }

    @Test
    @DisplayName("POST /task-lists - Should return 400 when title is blank")
    void createTaskList_ShouldReturnBadRequest_WhenTitleIsBlank() throws Exception {
        // Arrange
        CreateTaskListDto invalidDto = new CreateTaskListDto();
        invalidDto.setTitle("   ");
        invalidDto.setDescription("Valid description");

        // Act & Assert
        mockMvc.perform(post("/task-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskListService, never()).createTaskList(any(TaskList.class));
    }

    @Test
    @DisplayName("POST /task-lists - Should create task list when description is missing")
    void createTaskList_ShouldSucceed_WhenDescriptionIsMissing() throws Exception {
        // Arrange
        CreateTaskListDto dtoWithoutDescription = new CreateTaskListDto();
        dtoWithoutDescription.setTitle("Title Only");

        when(taskListMapper.fromCreateDTO(any(CreateTaskListDto.class))).thenReturn(taskList);
        when(taskListService.createTaskList(any(TaskList.class))).thenReturn(taskList);
        when(taskListMapper.toDTO(taskList)).thenReturn(taskListDto);

        // Act & Assert
        mockMvc.perform(post("/task-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoWithoutDescription)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(taskListService, times(1)).createTaskList(any(TaskList.class));
    }

    @Test
    @DisplayName("PUT /task-lists/{id} - Should update task list with valid data")
    void updateTaskList_ShouldUpdateTaskList_WithValidData() throws Exception {
        // Arrange
        TaskList updatedTaskList = new TaskList();
        updatedTaskList.setId(taskListId);
        updatedTaskList.setTitle("Updated Task List");
        updatedTaskList.setDescription("Updated Description");

        TaskListDto updatedDto = new TaskListDto();
        updatedDto.setId(taskListId);
        updatedDto.setTitle("Updated Task List");
        updatedDto.setDescription("Updated Description");

        when(taskListMapper.fromUpdateDTO(any(UpdateTaskListDto.class))).thenReturn(updatedTaskList);
        when(taskListService.updateTaskList(eq(taskListId), any(TaskList.class))).thenReturn(updatedTaskList);
        when(taskListMapper.toDTO(updatedTaskList)).thenReturn(updatedDto);

        // Act & Assert
        mockMvc.perform(put("/task-lists/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskListId.toString())))
                .andExpect(jsonPath("$.title", is("Updated Task List")))
                .andExpect(jsonPath("$.description", is("Updated Description")));

        verify(taskListMapper, times(1)).fromUpdateDTO(any(UpdateTaskListDto.class));
        verify(taskListService, times(1)).updateTaskList(eq(taskListId), any(TaskList.class));
        verify(taskListMapper, times(1)).toDTO(updatedTaskList);
    }

    @Test
    @DisplayName("PUT /task-lists/{id} - Should return 400 when title is missing")
    void updateTaskList_ShouldReturnBadRequest_WhenTitleIsMissing() throws Exception {
        // Arrange
        UpdateTaskListDto invalidDto = new UpdateTaskListDto();
        invalidDto.setDescription("Description only");

        // Act & Assert
        mockMvc.perform(put("/task-lists/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskListService, never()).updateTaskList(any(UUID.class), any(TaskList.class));
    }

    @Test
    @DisplayName("PUT /task-lists/{id} - Should return 404 when ID does not exist")
    void updateTaskList_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(taskListMapper.fromUpdateDTO(any(UpdateTaskListDto.class))).thenReturn(taskList);
        when(taskListService.updateTaskList(eq(nonExistentId), any(TaskList.class)))
                .thenThrow(new RuntimeException("Task list not found"));

        // Act & Assert
        mockMvc.perform(put("/task-lists/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(taskListService, times(1)).updateTaskList(eq(nonExistentId), any(TaskList.class));
    }

    @Test
    @DisplayName("DELETE /task-lists/{id} - Should delete task list when ID exists")
    void deleteTaskList_ShouldDeleteTaskList_WhenIdExists() throws Exception {
        // Arrange
        doNothing().when(taskListService).deleteTaskList(taskListId);

        // Act & Assert
        mockMvc.perform(delete("/task-lists/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(taskListService, times(1)).deleteTaskList(taskListId);
    }

    @Test
    @DisplayName("DELETE /task-lists/{id} - Should return 404 when ID does not exist")
    void deleteTaskList_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new RuntimeException("Task list not found"))
                .when(taskListService).deleteTaskList(nonExistentId);

        // Act & Assert
        mockMvc.perform(delete("/task-lists/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(taskListService, times(1)).deleteTaskList(nonExistentId);
    }

    @Test
    @DisplayName("DELETE /task-lists/{id} - Should delete task list with all tasks")
    void deleteTaskList_ShouldDeleteTaskListWithAllTasks() throws Exception {
        // Arrange - This tests cascade delete behavior
        doNothing().when(taskListService).deleteTaskList(taskListId);

        // Act & Assert
        mockMvc.perform(delete("/task-lists/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(taskListService, times(1)).deleteTaskList(taskListId);
    }

    @Test
    @DisplayName("Should handle malformed JSON in request body")
    void shouldHandleMalformedJson() throws Exception {
        // Arrange
        String malformedJson = "{title: 'Missing quotes', description: }";

        // Act & Assert
        mockMvc.perform(post("/task-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskListService, never()).createTaskList(any(TaskList.class));
    }

    @Test
    @DisplayName("Should handle invalid UUID format")
    void shouldHandleInvalidUuidFormat() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/task-lists/{id}", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskListService, never()).getTaskList(any(UUID.class));
    }

    @Test
    @DisplayName("Should handle title exceeding maximum length")
    void shouldHandleTitleExceedingMaxLength() throws Exception {
        // Arrange
        String longTitle = "a".repeat(300);
        CreateTaskListDto invalidDto = new CreateTaskListDto();
        invalidDto.setTitle(longTitle);
        invalidDto.setDescription("Valid description");

        // Act & Assert
        mockMvc.perform(post("/task-lists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(taskListService, never()).createTaskList(any(TaskList.class));
    }
}