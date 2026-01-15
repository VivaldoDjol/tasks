package com.gozzerks.taskflow.mappers;

import com.gozzerks.taskflow.domain.dto.TaskListDTO;
import com.gozzerks.taskflow.domain.entities.TaskList;

public interface TaskListMapper {
    TaskList fromDTO(TaskListDTO taskListDTO);
    TaskListDTO toDTO(TaskList taskList);
}
