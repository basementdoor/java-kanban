package kanban.service;

import kanban.dto.TaskNewDto;
import kanban.dto.TaskDto;
import kanban.dto.TaskUpdateDto;

import java.util.List;

public interface TaskService {

    TaskDto createTask(TaskNewDto taskNewDto);

    TaskDto updateTask(Long taskId, TaskUpdateDto updateDto);

    TaskDto getTaskById(Long taskId);

    void deleteTaskById(Long taskId);

    void deleteAllTasks();

    List<TaskDto> getAllTasks();
}
