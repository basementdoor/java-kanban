package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Status;

import static util.Status.IN_PROGRESS;
import static util.Status.NEW;

public class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void managerSetup() {
        taskManager = new InMemoryTaskManager();
    }

//  6.  проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id
    @Test
    public void createDifferentTasksAndFindItByIdTest() {
        Task task = new Task(1, "task", "desc", Status.NEW);
        Epic epic = new Epic(2, "epic", "desc");
        Subtask subtask = new Subtask(3, "subtask", "desc", Status.IN_PROGRESS, epic.getId());
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()), "Задача не была создана");
        Assertions.assertEquals(epic, taskManager.getEpicById(epic.getId()), "Эпик не был создан");
        Assertions.assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()), "Подзадача не была создана");
    }

//  7.  проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера
    @Test
    public void manualIdAndGeneratedIdNotConflictTest() {
        var id = 1;
        Task manualTask = new Task(id, "manualTask", "desc", Status.NEW);
        taskManager.getTasks().put(id, manualTask);

        Assertions.assertTrue(taskManager.getTasks().containsKey(manualTask.getId()));

        Task generatedTask = new Task("generatedTask", "desc", Status.NEW);
        taskManager.createTask(generatedTask);

        var manualId = taskManager.getTaskById(manualTask.getId()).getId();
        var generatedId = taskManager.getTaskById(generatedTask.getId()).getId();
        Assertions.assertNotEquals(manualId, generatedId);
    }

//  8.  создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    public void taskFieldsSameAfterAddToManagerTest() {
        Task taskBeforeManager = new Task(1, "task", "desc", Status.NEW);
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
        Task task = new Task("First task", "Reaaallyy important task", NEW);
        taskManager.createTask(task);
        Assertions.assertTrue(taskManager.getTasks().size() == 1);

        Epic epic = new Epic("First epic", "Contains one task");
        taskManager.createEpic(epic);
        Assertions.assertTrue(taskManager.getEpics().size() == 1);

        Subtask subtask = new Subtask("Think", "For first epic", NEW, epic.getId());
        taskManager.createSubtask(subtask);
        Assertions.assertTrue(taskManager.getSubtasks().size() == 1);

        taskManager.deleteAllTasks();
        Assertions.assertEquals(0, taskManager.getTasks().size());
        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    public void deleteDifferentTasksByIdTest() {
        Task task = new Task("First task", "Reaaallyy important task", NEW);
        taskManager.createTask(task);
        Assertions.assertTrue(taskManager.getTasks().containsKey(task.getId()));

        Epic epic = new Epic("First epic", "Contains one task");
        taskManager.createEpic(epic);
        Assertions.assertTrue(taskManager.getEpics().containsKey(epic.getId()));

        Subtask subtask = new Subtask("Think", "For first epic", NEW, epic.getId());
        taskManager.createSubtask(subtask);
        Assertions.assertTrue(taskManager.getSubtasks().containsKey(subtask.getId()));

        taskManager.deleteTaskById(task.getId());
        Assertions.assertFalse(taskManager.getTasks().containsKey(task.getId()));

        taskManager.deleteSubtaskById(subtask.getId());
        Assertions.assertFalse(taskManager.getSubtasks().containsKey(subtask.getId()));

        taskManager.deleteEpicById(epic.getId());
        Assertions.assertFalse(taskManager.getEpics().containsKey(epic.getId()));
    }

    @Test
    public void updateTaskTest() {
        Task task = new Task("First task", "desc", NEW);
        taskManager.createTask(task);

        taskManager.updateTask(new Task(task.getId(), "New name", "New desc", IN_PROGRESS));

        var updatedTask = taskManager.getTaskById(task.getId());
        Assertions.assertEquals("New name", updatedTask.getName());
        Assertions.assertEquals("New desc", updatedTask.getDescription());
        Assertions.assertEquals(IN_PROGRESS, updatedTask.getTaskStatus());
    }

    @Test
    public void updateSubtaskTest() {
        Epic epic = new Epic("First epic", "Contains one task");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Think", "For first epic", NEW, epic.getId());
        taskManager.createSubtask(subtask);

        taskManager.updateSubtask(new Subtask(subtask.getId(), "New name", "New desc", IN_PROGRESS,
                epic.getId()));

        var updatedSubtask = taskManager.getSubtaskById(subtask.getId());
        Assertions.assertEquals("New name", updatedSubtask.getName());
        Assertions.assertEquals("New desc", updatedSubtask.getDescription());
        Assertions.assertEquals(IN_PROGRESS, updatedSubtask.getTaskStatus());
    }

    @Test
    public void updateEpicTest() {
        Epic epic = new Epic("First epic", "Contains one task");
        taskManager.createEpic(epic);

        taskManager.updateEpic(new Epic(epic.getId(), "New name", "New desc"));

        var updatedEpic = taskManager.getEpicById(epic.getId());
        Assertions.assertEquals("New name", updatedEpic.getName());
        Assertions.assertEquals("New desc", updatedEpic.getDescription());
    }

    @Test
    public void updateEpicStatusTest() {
        Epic epic = new Epic("First epic", "Contains one task");
        taskManager.createEpic(epic);

        Subtask newTask = new Subtask("newTask", "For first epic", NEW, epic.getId());
        taskManager.createSubtask(newTask);
        Assertions.assertEquals(NEW, epic.getTaskStatus());

        Subtask inProgressTask = new Subtask("inProgressTask", "For first epic", IN_PROGRESS, epic.getId());
        taskManager.createSubtask(inProgressTask);

        Assertions.assertEquals(IN_PROGRESS, epic.getTaskStatus());
    }
}
