package manager;

import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Status;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InHistoryManagerTest {

    private TaskManager manager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        for (Task task : historyManager.getHistory()) {
            historyManager.remove(task.getId());
        }
    }

//  9.  убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных
    @Test
    void addTaskClone_previousVersionNotEqualNewVersion() {
        Task task = new Task(1, "task", "desc", Status.NEW);
        historyManager.add(task);
        assertTrue(historyManager.getHistory().size() != 0);

        task.setName("new name");
        task.setDescription("new desc");
        task.setTaskStatus(Status.DONE);
        var taskFromHistory = historyManager.getHistory().get(0);

        Assertions.assertEquals(task, taskFromHistory);
        Assertions.assertNotEquals(task.getName(), taskFromHistory.getName(), "Имена совпадают");
        Assertions.assertNotEquals(task.getDescription(), taskFromHistory.getDescription());
        Assertions.assertNotEquals(task.getTaskStatus(), taskFromHistory.getTaskStatus());
    }

    @Test
    void removeTaskFromHistory() {
        Task task = new Task(1, "task", "desc", Status.NEW);
        manager.createTask(task);
        manager.getTaskById(task.getId());
        manager.deleteTaskById(task.getId());

        Assertions.assertTrue(manager.getHistory().isEmpty(), "История не пуста после удаления");
    }

    @Test
    void shouldBeNoDuplicatesInHistory() {
        Task task = new Task(1, "task", "desc", Status.NEW);
        manager.createTask(task);
        manager.getTaskById(task.getId());

        ArrayList<Task> history = manager.getHistory();
        Assertions.assertEquals(1, history.size(), "История содержит дубликаты");
        Assertions.assertEquals(task.getId(), history.get(0).getId());
    }

    @Test
    void removeTaskFromMiddleOfHistory() {
        Task task1 = new Task(1, "task", "desc", Status.NEW);
        manager.createTask(task1);
        Task task2 = new Task(2, "task", "desc", Status.NEW);
        manager.createTask(task2);
        Task task3 = new Task(3, "task", "desc", Status.NEW);
        manager.createTask(task3);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task3.getId());

        manager.deleteTaskById(task2.getId());

        ArrayList<Task> history = manager.getHistory();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, history.size(), "Задача не удалилась"),
                () -> Assertions.assertEquals(task1.getId(), history.get(0).getId(), "Первая задача не на месте"),
                () -> Assertions.assertEquals(task3.getId(), history.get(1).getId(), "Третья задача не на месте")
        );
    }
}
