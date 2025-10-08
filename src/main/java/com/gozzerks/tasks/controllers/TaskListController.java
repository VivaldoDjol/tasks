package com.gozzerks.tasks.controllers;

import com.gozzerks.tasks.domain.dto.TaskListDTO;
import com.gozzerks.tasks.domain.entities.TaskList;
import com.gozzerks.tasks.mappers.TaskListMapper;
import com.gozzerks.tasks.services.TaskListService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/task-lists")
public class TaskListController {
    private final TaskListService taskListService;
    private final TaskListMapper taskListMapper;

    public TaskListController(TaskListService taskListService, TaskListMapper taskListMapper) {
        this.taskListService = taskListService;
        this.taskListMapper = taskListMapper;
    }

    @GetMapping
    public List<TaskListDTO> listTaskLists() {
        return taskListService.listTaskLists()
                .stream()
                .map(taskListMapper::toDTO)
                .toList();
    }
}
