package kanban.service;

import kanban.dto.SubtaskDto;
import kanban.dto.SubtaskNewDto;
import kanban.dto.SubtaskUpdateDto;
import kanban.exception.NotFoundException;
import kanban.mapper.SubtaskMapper;
import kanban.model.Subtask;
import kanban.reposritory.SubtaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
// TODO добавить проверку существования эпика и пересечения времени
public class SubtaskServiceImpl implements SubtaskService {

    private final SubtaskRepository subtaskRepository;

    @Override
    @Transactional
    public SubtaskDto createSubtask(SubtaskNewDto subtaskDto) {
        Subtask subtask = SubtaskMapper.toSubtask(subtaskDto);
        return SubtaskMapper.toDto(subtaskRepository.save(subtask));
    }

    @Override
    public SubtaskDto updateSubtask(Long taskId, SubtaskUpdateDto updateDto) {
        return null;
    }

    @Override
    public SubtaskDto getSubtaskById(Long taskId) {
        Subtask subtask = throwIfTaskNotExist(taskId);
        return SubtaskMapper.toDto(subtask);
    }

    @Override
    @Transactional
    public void deleteSubtaskById(Long taskId) {
        throwIfTaskNotExist(taskId);
        subtaskRepository.deleteById(taskId);
    }

    @Override
    @Transactional
    public void deleteAllSubtasks() {
        subtaskRepository.deleteAll();
    }

    @Override
    public List<SubtaskDto> getAllSubtasks() {
        List<Subtask> subtasks = subtaskRepository.findAll();
        return subtasks.stream()
                .map(SubtaskMapper::toDto)
                .toList();
    }

    private Subtask throwIfTaskNotExist(Long id) {
        return subtaskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Задача с ID: %s не найдена".formatted(id)));
    }
}
