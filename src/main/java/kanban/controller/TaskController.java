package kanban.controller;

import jakarta.validation.Valid;
import kanban.dto.TaskNewDto;
import kanban.dto.TaskDto;
import kanban.dto.TaskUpdateDto;
import kanban.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@Valid @RequestBody TaskNewDto taskNewDto) {
        return taskService.createTask(taskNewDto);
    }

    @PatchMapping("/{taskId}")
    public TaskDto updateTask(@PathVariable Long taskId,
                              @Valid @RequestBody TaskUpdateDto updateDto) {
        return taskService.updateTask(taskId, updateDto);
    }

    @GetMapping("/{taskId}")
    public TaskDto getTaskById(@PathVariable Long taskId) {
        return taskService.getTaskById(taskId);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTaskById(@PathVariable Long taskId) {
        taskService.deleteTaskById(taskId);
    }

    @GetMapping
    public List<TaskDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllTasks() {
        taskService.deleteAllTasks();
    }


}
