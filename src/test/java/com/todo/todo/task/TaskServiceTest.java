package com.todo.todo.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskService = new TaskService(taskRepository);
    }

    @Test
    void createTask_savesAndReturnsDto() {
        Task toSave = new Task("title", "desc");
        Task saved = new Task("title", "desc");
        saved.markCompleted(); // just to ensure mapping picks up field

        given(taskRepository.save(org.mockito.ArgumentMatchers.any(Task.class))).willReturn(toSave);

        TaskDto dto = taskService.createTask("title", "desc");

        then(taskRepository).should().save(org.mockito.ArgumentMatchers.any(Task.class));
        assertThat(dto.title()).isEqualTo("title");
        assertThat(dto.description()).isEqualTo("desc");
        assertThat(dto.completed()).isFalse();
    }

    @Test
    void getRecentIncompleteTasks_returnsMappedDtos() {
        Task t1 = new Task("t1", "d1");
        Task t2 = new Task("t2", "d2");
        given(taskRepository.findTop5ByCompletedFalseOrderByCreatedAtDesc())
                .willReturn(List.of(t1, t2));

        List<TaskDto> result = taskService.getRecentIncompleteTasks();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).title()).isEqualTo("t1");
        assertThat(result.get(1).title()).isEqualTo("t2");
    }

    @Test
    void markTaskDone_marksAndSaves() {
        Task task = new Task("t", "d");
        given(taskRepository.findById(1L)).willReturn(Optional.of(task));
        given(taskRepository.save(task)).willReturn(task);

        TaskDto dto = taskService.markTaskDone(1L);

        assertThat(dto.completed()).isTrue();
        then(taskRepository).should().save(task);
    }

    @Test
    void markTaskDone_throwsWhenNotFound() {
        given(taskRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.markTaskDone(1L))
                .isInstanceOf(TaskNotFoundException.class);
    }
}

