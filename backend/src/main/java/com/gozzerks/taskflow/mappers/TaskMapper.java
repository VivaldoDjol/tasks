package com.gozzerks.taskflow.mappers;

import com.gozzerks.taskflow.domain.dto.TaskDTO;
import com.gozzerks.taskflow.domain.entities.Task;

public interface TaskMapper {

    Task fromDTO(TaskDTO taskDTO);
    TaskDTO toDTO(Task task);

}
