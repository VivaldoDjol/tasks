package com.gozzerks.tasks.domain.dto;

import com.gozzerks.tasks.domain.entities.TaskPriority;
import com.gozzerks.tasks.domain.entities.TaskStatus;

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
