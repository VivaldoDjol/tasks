package com.gozzerks.tasks.services.impl;

import com.gozzerks.tasks.domain.entities.TaskList;
import com.gozzerks.tasks.repositories.TaskListRepository;
import com.gozzerks.tasks.services.TaskListService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskListServiceImpl implements TaskListService {
    private final TaskListRepository taskListRepository;

    public TaskListServiceImpl(TaskListRepository taskListRepository) {
        this.taskListRepository = taskListRepository;
    }

    @Override
    public List<TaskList> listTaskLists() {
        return taskListRepository.findAll();
    }


}
