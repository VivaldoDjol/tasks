package com.gozzerks.taskflow.domain.dto;

import com.gozzerks.taskflow.domain.entities.TaskPriority;
import com.gozzerks.taskflow.domain.entities.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskDTO(
        UUID id,
        String title,
        String description,
        LocalDateTime dueDate,
        TaskPriority priority,
        TaskStatus status
) {


}
