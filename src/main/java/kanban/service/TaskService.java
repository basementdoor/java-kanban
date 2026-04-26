package kanban.service;

import kanban.dto.NewTaskDto;
import kanban.dto.TaskDto;

public interface TaskService {

    TaskDto createTask(NewTaskDto newTaskDto);
}
