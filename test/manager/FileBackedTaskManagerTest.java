package manager;

import exception.ManagerReadException;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Status;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return (FileBackedTaskManager) Managers.getDefault();
    }

    @Test
    public void saveAndLoadManagerFromFileTest() {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("tasks", ".csv");

            TaskManager manager = new FileBackedTaskManager(tempFile);

            Task task = new Task("Task", "Test task", Status.IN_PROGRESS, LocalDateTime.now(),
                    Duration.of(9, ChronoUnit.HOURS));
            manager.createTask(task);
            manager.getTaskById(task.getId());

            Epic epic = new Epic("Epic", "Test epic");
            manager.createTask(epic);

            Subtask subtask = new Subtask("Subtask", "Test subtask", Status.NEW, epic.getId(),
                    LocalDateTime.of(2024, 7, 15, 18, 30), Duration.of(700, ChronoUnit.SECONDS));
            manager.createTask(subtask);

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

            // сначала проверяем количество тасок
            Assertions.assertEquals(manager.getTasks().size(), loadedManager.getTasks().size());

            // убеждаемся, что задачи те же самые
            Assertions.assertEquals(task, loadedManager.tasks.get(task.getId()));
            Assertions.assertEquals(subtask, loadedManager.tasks.get(subtask.getId()));
            Assertions.assertEquals(epic, loadedManager.tasks.get(epic.getId()));

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

    @Test
    void loadFromNotExistFileExceptionTest() {
        File errorFile = new File("errorFile.csv");
        Assertions.assertThrows(ManagerReadException.class, () -> FileBackedTaskManager.loadFromFile(errorFile));
    }

}
