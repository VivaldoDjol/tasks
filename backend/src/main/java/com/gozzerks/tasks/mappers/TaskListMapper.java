package com.gozzerks.tasks.mappers;

import com.gozzerks.tasks.domain.dto.TaskListDTO;
import com.gozzerks.tasks.domain.entities.TaskList;

public interface TaskListMapper {
    TaskList fromDTO(TaskListDTO taskListDTO);
    TaskListDTO toDTO(TaskList taskList);
}
