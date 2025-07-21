package manager;

import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InHistoryManagerTest {

    private TaskManager manager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        manager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        historyManager.getHistory().forEach(
                task -> historyManager.remove(task.getId())
        );
    }

//  9.  убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных
    @Test
    void changedTaskShouldNotBeEqualPreviousVersionTest() {
        Task task = new Task(1, "task", "desc", Status.NEW, LocalDateTime.now(),
                Duration.of(1, ChronoUnit.DAYS));
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
    void removeTaskFromHistoryTest() {
        Task task = new Task(1, "task", "desc", Status.NEW, LocalDateTime.now(),
                Duration.of(1, ChronoUnit.DAYS));
        manager.createTask(task);
        manager.getTaskById(task.getId());
        manager.deleteTaskById(task.getId());

        Assertions.assertTrue(manager.getHistory().isEmpty(), "История не пуста после удаления");
    }

    @Test
    void shouldBeNoDuplicatesInHistoryTest() {
        Task task = new Task(1, "task", "desc", Status.NEW, LocalDateTime.now(),
                Duration.of(1, ChronoUnit.DAYS));
        manager.createTask(task);
        manager.getTaskById(task.getId());

        ArrayList<Task> history = manager.getHistory();
        Assertions.assertEquals(1, history.size(), "История содержит дубликаты");
        Assertions.assertEquals(task.getId(), history.get(0).getId());
    }

    @Test
    void removeTaskFromMiddleOfHistoryTest() {
        Task task1 = new Task(1, "task", "desc", Status.NEW, LocalDateTime.now(),
                Duration.of(1, ChronoUnit.HOURS));
        manager.createTask(task1);
        Task task2 = new Task(2, "task", "desc", Status.NEW,
                LocalDateTime.now().plusDays(1), Duration.of(1, ChronoUnit.HOURS));
        manager.createTask(task2);
        Task task3 = new Task(3, "task", "desc", Status.NEW,
                LocalDateTime.now().plusDays(2), Duration.of(1, ChronoUnit.HOURS));
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

    @Test
    void removeTaskFromBeginningOfHistoryTest() {
        Task task1 = new Task(1, "task", "desc", Status.NEW, LocalDateTime.now(),
                Duration.of(1, ChronoUnit.HOURS));
        manager.createTask(task1);
        Task task2 = new Task(2, "task", "desc", Status.NEW,
                LocalDateTime.now().plusDays(1), Duration.of(1, ChronoUnit.HOURS));
        manager.createTask(task2);
        Task task3 = new Task(3, "task", "desc", Status.NEW,
                LocalDateTime.now().plusDays(2), Duration.of(1, ChronoUnit.HOURS));
        manager.createTask(task3);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task3.getId());

        manager.deleteTaskById(task1.getId());

        ArrayList<Task> history = manager.getHistory();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, history.size(), "Задача не удалилась"),
                () -> Assertions.assertEquals(task2.getId(), history.get(0).getId(), "Вторая задача не на месте"),
                () -> Assertions.assertEquals(task3.getId(), history.get(1).getId(), "Третья задача не на месте")
        );
    }

    @Test
    void removeTaskFromEndOfHistoryTest() {
        Task task1 = new Task(1, "task", "desc", Status.NEW, LocalDateTime.now(),
                Duration.of(1, ChronoUnit.HOURS));
        manager.createTask(task1);
        Task task2 = new Task(2, "task", "desc", Status.NEW,
                LocalDateTime.now().plusDays(1), Duration.of(1, ChronoUnit.HOURS));
        manager.createTask(task2);
        Task task3 = new Task(3, "task", "desc", Status.NEW,
                LocalDateTime.now().plusDays(2), Duration.of(1, ChronoUnit.HOURS));
        manager.createTask(task3);

        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        manager.getTaskById(task3.getId());

        manager.deleteTaskById(task3.getId());

        ArrayList<Task> history = manager.getHistory();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, history.size(), "Задача не удалилась"),
                () -> Assertions.assertEquals(task1.getId(), history.get(0).getId(), "Первая задача не на месте"),
                () -> Assertions.assertEquals(task2.getId(), history.get(1).getId(), "Вторая задача не на месте")
        );
    }

    @Test
    void shouldBeEmptyHistoryTest() {
        Task task = new Task(1, "task", "desc", Status.NEW, LocalDateTime.now(),
                Duration.of(1, ChronoUnit.DAYS));
        manager.createTask(task);

        Task task2 = new Task(2, "task", "desc", Status.NEW, LocalDateTime.now().plusDays(2),
                Duration.of(1, ChronoUnit.DAYS));
        manager.createTask(task2);

        Assertions.assertTrue(manager.getHistory().isEmpty());
    }
}
