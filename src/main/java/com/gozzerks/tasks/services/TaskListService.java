package com.gozzerks.tasks.services;

import com.gozzerks.tasks.domain.entities.TaskList;

import java.util.List;

public interface TaskListService {
    List<TaskList> listTaskLists();
}
