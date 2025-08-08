package manager;

import exception.TaskIntersectionException;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.TreeSet;

import static util.Status.IN_PROGRESS;
import static util.Status.NEW;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void managerSetup() {
        taskManager = createTaskManager();
    }

    //  6.  проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
    @Test
    public void createDifferentTasksAndFindItByIdTest() {
        Task task = new Task("task", "desc", Status.NEW, LocalDateTime.now(),
                Duration.of(180, ChronoUnit.MINUTES));
        taskManager.createTask(task);
        Epic epic = new Epic("epic", "desc");
        taskManager.createTask(epic);
        Subtask subtask = new Subtask("subtask", "desc", Status.IN_PROGRESS, epic.getId(),
                LocalDateTime.of(2025, 7, 15, 18, 30), Duration.of(70, ChronoUnit.MINUTES));
        taskManager.createTask(subtask);

        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()), "Задача не была создана");
        Assertions.assertEquals(epic, taskManager.getTaskById(epic.getId()), "Эпик не был создан");
        Assertions.assertEquals(subtask, taskManager.getTaskById(subtask.getId()), "Подзадача не была создана");
    }

    //  7.  проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера
    @Test
    public void manualIdAndGeneratedIdNotConflictTest() {
        var id = 1;
        Task manualTask = new Task(id, "manualTask", "desc", Status.NEW, LocalDateTime.now(),
                Duration.of(1, ChronoUnit.DAYS));
        taskManager.getTasks().put(id, manualTask);

        Assertions.assertTrue(taskManager.getTasks().containsKey(manualTask.getId()));

        Task generatedTask = new Task("generatedTask", "desc", Status.NEW,
                LocalDateTime.of(2025, 8, 15, 10, 1), Duration.of(1, ChronoUnit.DAYS));
        taskManager.createTask(generatedTask);

        var manualId = taskManager.getTaskById(manualTask.getId()).getId();
        var generatedId = taskManager.getTaskById(generatedTask.getId()).getId();
        Assertions.assertNotEquals(manualId, generatedId);
    }

    //  8.  создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    public void taskFieldsSameAfterAddToManagerTest() {
        Task taskBeforeManager = new Task(1, "task", "desc", Status.NEW, LocalDateTime.now(),
                Duration.of(60, ChronoUnit.MINUTES));
        taskManager.createTask(taskBeforeManager);
        Task taskAfterManager = taskManager.getTaskById(taskBeforeManager.getId());

        Assertions.assertEquals(taskBeforeManager.getId(), taskAfterManager.getId(), "ID не совпадают");
        Assertions.assertEquals(taskBeforeManager.getName(), taskAfterManager.getName(), "name не совпадают");
        Assertions.assertEquals(taskBeforeManager.getDescription(), taskAfterManager.getDescription(),
                "description не совпадают");
        Assertions.assertEquals(taskBeforeManager.getTaskStatus(), taskAfterManager.getTaskStatus(),
                "статусы не совпадают");
    }

    @Test
    public void deleteAllTasksTest() {
        Task task = new Task("First task", "Reaaallyy important task", NEW, LocalDateTime.now(),
                Duration.of(3, ChronoUnit.HOURS));
        taskManager.createTask(task);
        Assertions.assertTrue(taskManager.getTasks().size() == 1);

        Epic epic = new Epic("First epic", "Contains one task");
        taskManager.createTask(epic);
        Assertions.assertTrue(taskManager.getTasks().size() == 2);

        Subtask subtask = new Subtask("Think", "For first epic", NEW, epic.getId(),
                LocalDateTime.of(2025, 7, 15, 18, 30), Duration.of(70, ChronoUnit.MINUTES));
        taskManager.createTask(subtask);
        Assertions.assertTrue(taskManager.getTasks().size() == 3);

        taskManager.deleteAllTasks();
        Assertions.assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    public void deleteDifferentTasksByIdTest() {
        Task task = new Task("First task", "Reaaallyy important task", NEW, LocalDateTime.now(),
                Duration.of(20, ChronoUnit.MINUTES));
        taskManager.createTask(task);
        Assertions.assertTrue(taskManager.getTasks().containsKey(task.getId()));

        Epic epic = new Epic("First epic", "Contains one task");
        taskManager.createTask(epic);
        Assertions.assertTrue(taskManager.getTasks().containsKey(epic.getId()));

        Subtask subtask = new Subtask("Think", "For first epic", NEW, epic.getId(),
                LocalDateTime.of(2025, 7, 15, 18, 30), Duration.of(70, ChronoUnit.MINUTES));
        taskManager.createTask(subtask);
        Assertions.assertTrue(taskManager.getTasks().containsKey(subtask.getId()));

        taskManager.deleteTaskById(task.getId());
        Assertions.assertFalse(taskManager.getTasks().containsKey(task.getId()));

        taskManager.deleteTaskById(subtask.getId());
        Assertions.assertFalse(taskManager.getTasks().containsKey(subtask.getId()));

        taskManager.deleteTaskById(epic.getId());
        Assertions.assertFalse(taskManager.getTasks().containsKey(epic.getId()));
    }

    @Test
    public void updateTaskTest() {
        Task task = new Task("First task", "desc", NEW, LocalDateTime.now(),
                Duration.of(90, ChronoUnit.MINUTES));
        taskManager.createTask(task);

        taskManager.updateTask(new Task(task.getId(), "New name", "New desc", IN_PROGRESS,
                LocalDateTime.now().minusDays(1), Duration.of(1, ChronoUnit.HOURS)));

        var updatedTask = taskManager.getTaskById(task.getId());
        Assertions.assertEquals("New name", updatedTask.getName());
        Assertions.assertEquals("New desc", updatedTask.getDescription());
        Assertions.assertEquals(IN_PROGRESS, updatedTask.getTaskStatus());
    }

    @Test
    public void updateSubtaskTest() {
        Epic epic = new Epic("First epic", "Contains one task");
        taskManager.createTask(epic);
        Subtask subtask = new Subtask("Think", "For first epic", NEW, epic.getId(),
                LocalDateTime.now(), Duration.of(70, ChronoUnit.MINUTES));
        taskManager.createTask(subtask);

        taskManager.updateTask(new Subtask(subtask.getId(), "New name", "New desc", IN_PROGRESS,
                epic.getId(), LocalDateTime.now().minusDays(1), Duration.of(70, ChronoUnit.MINUTES)));

        var updatedSubtask = taskManager.getTaskById(subtask.getId());
        Assertions.assertEquals("New name", updatedSubtask.getName());
        Assertions.assertEquals("New desc", updatedSubtask.getDescription());
        Assertions.assertEquals(IN_PROGRESS, updatedSubtask.getTaskStatus());
    }

    @Test
    public void updateEpicTest() {
        Epic epic = new Epic("First epic", "Contains one task");
        taskManager.createTask(epic);

        taskManager.updateTask(new Epic(epic.getId(), "New name", "New desc"));

        var updatedEpic = taskManager.getTaskById(epic.getId());
        Assertions.assertEquals("New name", updatedEpic.getName());
        Assertions.assertEquals("New desc", updatedEpic.getDescription());
    }

    @Test
    public void updateEpicStatusTest() {
        Epic epic = new Epic("First epic", "Contains one task");
        taskManager.createTask(epic);

        Subtask newTask = new Subtask("newTask", "For first epic", NEW, epic.getId(),
                LocalDateTime.now(), Duration.of(70, ChronoUnit.MINUTES));
        taskManager.createTask(newTask);
        Assertions.assertEquals(NEW, epic.getTaskStatus());

        Subtask inProgressTask = new Subtask("inProgressTask", "For first epic", IN_PROGRESS, epic.getId(),
                LocalDateTime.now().plusDays(1), Duration.of(70, ChronoUnit.MINUTES));
        taskManager.createTask(inProgressTask);

        Assertions.assertEquals(IN_PROGRESS, epic.getTaskStatus());
    }

    @Test
    public void getTasksTest() {
        Epic epic = new Epic("First epic", "Contains one task");
        taskManager.createTask(epic);

        Subtask subtask = new Subtask("inProgressTask", "For first epic", IN_PROGRESS, epic.getId(),
                LocalDateTime.now(), Duration.of(4, ChronoUnit.HOURS));
        taskManager.createTask(subtask);

        Task task = new Task("task", "desc", NEW, LocalDateTime.of(2025, 10, 9, 6, 1),
                Duration.of(2, ChronoUnit.DAYS));
        taskManager.createTask(task);

        Assertions.assertAll(
                () -> Assertions.assertEquals(3, taskManager.getTasks().size()),
                () -> Assertions.assertTrue(taskManager.getTasks().containsKey(epic.getId())),
                () -> Assertions.assertTrue(taskManager.getTasks().containsKey(subtask.getId())),
                () -> Assertions.assertTrue(taskManager.getTasks().containsKey(task.getId()))
        );
    }

    @Test
    public void getHistoryReturnsCorrectHistoryTest() {
        Epic epic = new Epic("First epic", "Contains one task");
        taskManager.createTask(epic);
        taskManager.getTaskById(epic.getId());

        Subtask subtask = new Subtask("Subtask", "For first epic", IN_PROGRESS, epic.getId(),
                LocalDateTime.now(), Duration.of(4, ChronoUnit.HOURS));
        taskManager.createTask(subtask);
        taskManager.getTaskById(subtask.getId());

        Task task = new Task("Task", "desc", NEW, LocalDateTime.of(2025, 10, 9, 6, 1),
                Duration.of(2, ChronoUnit.DAYS));
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());

        ArrayList<Task> currentHistory = taskManager.getHistory();

        Assertions.assertAll(
                () -> Assertions.assertEquals(3, currentHistory.size()),
                () -> Assertions.assertEquals(epic.getId(), currentHistory.get(0).getId()),
                () -> Assertions.assertEquals(subtask.getId(), currentHistory.get(1).getId()),
                () -> Assertions.assertEquals(task.getId(), currentHistory.get(2).getId())
        );
    }

    @Test
    public void correctSimpleTasksPrioritizeTest() {

        Task lastTask = new Task("LastTask", "desc", NEW, LocalDateTime.now().plusDays(2),
                Duration.of(2, ChronoUnit.DAYS));
        taskManager.createTask(lastTask);

        Task firstTask = new Task("FirstTask", "desc", NEW, LocalDateTime.now(),
                Duration.of(2, ChronoUnit.HOURS));
        taskManager.createTask(firstTask);

        Task middleTask = new Task("MiddleTask", "desc", NEW, LocalDateTime.now().plusDays(1),
                Duration.of(3, ChronoUnit.HOURS));
        taskManager.createTask(middleTask);

        TreeSet<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        Assertions.assertAll(
                () -> Assertions.assertEquals(3, prioritizedTasks.size()),
                () -> Assertions.assertEquals(firstTask, prioritizedTasks.first()),
                () -> Assertions.assertEquals(lastTask, prioritizedTasks.last())
        );
    }

    @Test
    public void intersectionTasksExceptionTest() {

        Task firstTask = new Task("LastTask", "desc", NEW, LocalDateTime.now(),
                Duration.of(2, ChronoUnit.HOURS));
        taskManager.createTask(firstTask);

        Task secondTask = new Task("FirstTask", "desc", NEW, LocalDateTime.now().plusHours(1),
                Duration.of(2, ChronoUnit.HOURS));

        Assertions.assertThrows(TaskIntersectionException.class, () -> taskManager.createTask(secondTask));
    }


}
