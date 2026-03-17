package com.todo.todo.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank
        @Size(max = 255)
        String title,
        @Size(max = 10_000)
        String description
) {
}

