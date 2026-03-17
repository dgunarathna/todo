package com.todo.todo.task;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDto> listRecentIncomplete() {
        return taskService.getRecentIncompleteTasks();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto create(@Valid @RequestBody TaskRequest request) {
        return taskService.createTask(request.title(), request.description());
    }

    @PostMapping("/{id}/done")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markDone(@PathVariable Long id) {
        taskService.markTaskDone(id);
    }
}

