package kanban.controller;

import jakarta.validation.Valid;
import kanban.dto.TaskNewDto;
import kanban.dto.TaskDto;
import kanban.dto.TaskUpdateDto;
import kanban.service.SubtaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subtasks")
public class SubtaskController {

    private final SubtaskService subtaskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@Valid @RequestBody TaskNewDto taskNewDto) {
        return subtaskService.createTask(taskNewDto);
    }

    @PatchMapping("/{taskId}")
    public TaskDto updateTask(@PathVariable Long taskId,
                              @Valid @RequestBody TaskUpdateDto updateDto) {
        return subtaskService.updateTask(taskId, updateDto);
    }

    @GetMapping("/{taskId}")
    public TaskDto getTaskById(@PathVariable Long taskId) {
        return subtaskService.getTaskById(taskId);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskById(@PathVariable Long taskId) {
        subtaskService.deleteTaskById(taskId);
    }

    @GetMapping
    public List<TaskDto> getAllTasks() {
        return subtaskService.getAllTasks();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllTasks() {
        subtaskService.deleteAllTasks();
    }
}
