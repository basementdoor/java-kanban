package kanban.controller;

import jakarta.validation.Valid;
import kanban.dto.NewTaskDto;
import kanban.dto.TaskDto;
import kanban.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@Valid @RequestBody NewTaskDto newTaskDto) {
        return taskService.createTask(newTaskDto);
    }


}
