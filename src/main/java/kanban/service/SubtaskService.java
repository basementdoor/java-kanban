package kanban.service;

import kanban.dto.SubtaskDto;
import kanban.dto.SubtaskNewDto;
import kanban.dto.SubtaskUpdateDto;

import java.util.List;

public interface SubtaskService {

    SubtaskDto createSubtask(SubtaskNewDto subtaskDto);

    SubtaskDto updateSubtask(Long taskId, SubtaskUpdateDto updateDto);

    SubtaskDto getSubtaskById(Long taskId);

    void deleteSubtaskById(Long taskId);

    void deleteAllSubtasks();

    List<SubtaskDto> getAllSubtasks();
}
