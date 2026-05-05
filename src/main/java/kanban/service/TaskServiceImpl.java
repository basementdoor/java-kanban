package kanban.service;

import kanban.dto.TaskNewDto;
import kanban.dto.TaskDto;
import kanban.dto.TaskUpdateDto;
import kanban.exception.NotFoundException;
import kanban.exception.ValidationException;
import kanban.mapper.TaskMapper;
import kanban.model.Task;
import kanban.reposritory.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    @Transactional
    public TaskDto createTask(TaskNewDto taskNewDto) {
        validateNoIntersection(null, taskNewDto.getStartTime(), taskNewDto.getEndTime());
        Task task = taskRepository.save(TaskMapper.toTask(taskNewDto));
        return TaskMapper.toDto(task);
    }

    @Override
    @Transactional
    public TaskDto updateTask(Long taskId, TaskUpdateDto updateDto) {
        Task task = throwIfTaskNotExist(taskId);
        if (updateDto.getName() != null && !updateDto.getName().isBlank()) {
            task.setName(updateDto.getName());
        }

        if (updateDto.getDescription() != null && !updateDto.getDescription().isBlank()) {
            task.setDescription(updateDto.getDescription());
        }

        if (updateDto.getStatus() != null) {
            task.setStatus(updateDto.getStatus());
        }

        LocalDateTime newStart = updateDto.getStartTime() != null
                ? updateDto.getStartTime()
                : task.getStartTime();

        LocalDateTime newEnd = updateDto.getEndTime() != null
                ? updateDto.getEndTime()
                : task.getEndTime();

        validateNoIntersection(taskId, newStart, newEnd);
        task.setStartTime(newStart);
        task.setEndTime(newEnd);
        return TaskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public TaskDto getTaskById(Long taskId) {
        Task task = throwIfTaskNotExist(taskId);
        return TaskMapper.toDto(task);
    }

    @Override
    @Transactional
    public void deleteTaskById(Long taskId) {
        Task task = throwIfTaskNotExist(taskId);
        taskRepository.deleteById(task.getId());
    }

    @Override
    @Transactional
    public void deleteAllTasks() {
        taskRepository.deleteAll();
    }

    @Override
    public List<TaskDto> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(TaskMapper::toDto)
                .toList();
    }

    private Task throwIfTaskNotExist(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Задача с ID: %s не найдена".formatted(id)));
    }

    private void validateNoIntersection(Long taskId, LocalDateTime startTime, LocalDateTime endTime) {
        boolean exists = taskRepository.existsIntersection(
                taskId == null ? -1 : taskId,
                startTime,
                endTime
        );

        if (exists) {
            throw new ValidationException("Время выполнения задачи пересекается с уже существующей");
        }

    }
}
