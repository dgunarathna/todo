package com.todo.todo.task;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskDto createTask(String title, String description) {
        Task task = new Task(title, description);
        Task saved = taskRepository.save(task);
        return TaskDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getRecentIncompleteTasks() {
        return taskRepository.findTop5ByCompletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(TaskDto::fromEntity)
                .toList();
    }

    public TaskDto markTaskDone(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        task.markCompleted();
        Task saved = taskRepository.save(task);
        return TaskDto.fromEntity(saved);
    }
}

