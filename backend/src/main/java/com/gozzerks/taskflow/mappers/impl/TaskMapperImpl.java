package com.gozzerks.taskflow.mappers.impl;

import com.gozzerks.taskflow.domain.dto.TaskDTO;
import com.gozzerks.taskflow.domain.entities.Task;
import com.gozzerks.taskflow.mappers.TaskMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskMapperImpl implements TaskMapper {
    @Override
    public Task fromDTO(TaskDTO taskDTO) {
        return new Task(
                taskDTO.id(),
                taskDTO.title(),
                taskDTO.description(),
                taskDTO.dueDate(),
                taskDTO.status(),
                taskDTO.priority(),
                null,
                null,
                null
        );
    }

    @Override
    public TaskDTO toDTO(Task task) {
        return new TaskDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus()
        );
    }
}
