package com.gozzerks.tasks.mappers.impl;

import com.gozzerks.tasks.domain.dto.TaskDTO;
import com.gozzerks.tasks.domain.entities.Task;
import com.gozzerks.tasks.mappers.TaskMapper;
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
