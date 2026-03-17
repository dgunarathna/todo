package com.todo.todo.task;

import java.time.LocalDateTime;

public record TaskDto(
        Long id,
        String title,
        String description,
        LocalDateTime createdAt,
        boolean completed
) {
    public static TaskDto fromEntity(Task task) {
        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCreatedAt(),
                task.isCompleted()
        );
    }
}

