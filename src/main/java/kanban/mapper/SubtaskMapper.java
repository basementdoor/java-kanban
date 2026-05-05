package kanban.mapper;

import kanban.dto.SubtaskDto;
import kanban.dto.SubtaskNewDto;
import kanban.model.Subtask;
import kanban.util.Status;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SubtaskMapper {

    public SubtaskDto toDto(Subtask subtask) {
        return SubtaskDto.builder()
                .id(subtask.getId())
                .name(subtask.getName())
                .description(subtask.getDescription())
                .status(subtask.getStatus())
                .startTime(subtask.getStartTime())
                .endTime(subtask.getEndTime())
                .epicId(subtask.getEpicId())
                .build();
    }

    public Subtask toSubtask(SubtaskNewDto subtaskDto) {
        return Subtask.builder()
                .name(subtaskDto.getName())
                .description(subtaskDto.getDescription())
                .status(Status.NEW)
                .startTime(subtaskDto.getStartTime())
                .endTime(subtaskDto.getEndTime())
                .epicId(subtaskDto.getEpicId())
                .build();
    }
}
