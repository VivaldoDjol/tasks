package com.gozzerks.tasks.controllers;

import com.gozzerks.tasks.domain.dto.TaskDTO;
import com.gozzerks.tasks.domain.entities.Task;
import com.gozzerks.tasks.mappers.TaskMapper;
import com.gozzerks.tasks.services.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/tasks-lists/{task_list_id}/tasks")
public class TasksController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TasksController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping
    public List<TaskDTO> listTasks(@PathVariable("task_list_id") UUID taskListId) {
        return taskService.listTasks(taskListId)
                .stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    @PostMapping
    public TaskDTO createTask(@PathVariable("task_list_id") UUID taskListId, @RequestBody TaskDTO taskDTO) {
        Task createdTask = taskService.createTask(
                taskListId,
                taskMapper.fromDTO(taskDTO)
        );
        return taskMapper.toDTO(createdTask);
    }
}