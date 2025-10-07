package com.gozzerks.tasks.mappers;

import com.gozzerks.tasks.domain.dto.TaskDTO;
import com.gozzerks.tasks.domain.entities.Task;

public interface TaskMapper {

    Task fromDTO(TaskDTO taskDTO);
    TaskDTO toDTO(Task task);

}
