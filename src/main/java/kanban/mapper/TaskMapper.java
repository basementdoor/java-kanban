package kanban.mapper;

import kanban.dto.TaskNewDto;
import kanban.dto.TaskDto;
import kanban.model.Task;
import kanban.util.Status;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TaskMapper {

    public TaskDto toDto(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .status(task.getStatus())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .build();
    }

    public Task toTask(TaskNewDto taskDto) {
        return Task.builder()
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .status(Status.NEW)
                .startTime(taskDto.getStartTime())
                .endTime(taskDto.getEndTime())
                .build();
    }
}
