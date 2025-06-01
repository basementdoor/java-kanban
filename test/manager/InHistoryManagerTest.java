package manager;

import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Status;

public class InHistoryManagerTest {

//  9.  убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных
    @Test
    void addTaskClone_previousVersionNotEqualNewVersion() {
        Task task = new Task(1, "task", "desc", Status.NEW);
        HistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.add(task);
        Assertions.assertTrue(historyManager.getHistory().size() != 0);

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
    void historyListNoMoreThan10Test() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task1 = new Task(1, "task", "desc", Status.NEW);
        Task task2 = new Task(2, "task", "desc", Status.NEW);
        Task task3 = new Task(3, "task", "desc", Status.NEW);
        Task task4 = new Task(4, "task", "desc", Status.NEW);
        Task task5 = new Task(5, "task", "desc", Status.NEW);
        Task task6 = new Task(6, "task", "desc", Status.NEW);
        Task task7 = new Task(7, "task", "desc", Status.NEW);
        Task task8 = new Task(8, "task", "desc", Status.NEW);
        Task task9 = new Task(9, "task", "desc", Status.NEW);
        Task task10 = new Task(10,  "task", "desc", Status.NEW);
        Task task11 = new Task(11,  "task", "desc", Status.NEW);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.add(task5);
        historyManager.add(task6);
        historyManager.add(task7);
        historyManager.add(task8);
        historyManager.add(task9);
        historyManager.add(task10);

        Assertions.assertEquals(10, historyManager.getHistory().size());

        historyManager.add(task11);

        Assertions.assertFalse(historyManager.getHistory().contains(task1));
        Assertions.assertEquals(10, historyManager.getHistory().size());
    }
}
