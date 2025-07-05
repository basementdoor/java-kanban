package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Status;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest {

    @Test
    public void saveAndLoadManagerFromFileTest() {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("tasks", ".csv");

            TaskManager manager = new FileBackedTaskManager(tempFile);

            Task task = new Task("Task", "Test task", Status.IN_PROGRESS);
            manager.createTask(task);
            manager.getTaskById(task.getId());

            Epic epic = new Epic("Epic", "Test epic");
            manager.createEpic(epic);

            Subtask subtask = new Subtask("Subtask", "Test subtask", Status.NEW, epic.getId());
            manager.createSubtask(subtask);

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            // сначала проверяем количество тасок
            Assertions.assertEquals(manager.getTasks().size(), loadedManager.getTasks().size());
            Assertions.assertEquals(manager.getSubtasks().size(), loadedManager.getSubtasks().size());
            Assertions.assertEquals(manager.getEpics().size(), loadedManager.getEpics().size());

            // убеждаемся, что задачи те же самые
            Assertions.assertEquals(task, loadedManager.tasks.get(task.getId()));
            Assertions.assertEquals(subtask, loadedManager.subtasks.get(subtask.getId()));
            Assertions.assertEquals(epic, loadedManager.epics.get(epic.getId()));

            // проверяем историю
            Assertions.assertEquals(manager.getHistory(), loadedManager.getHistory());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerReadException("Ошибка при создании файла");
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
    }

}
