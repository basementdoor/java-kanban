package model;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class SubtaskTest {

    private final int id = 1;

//   2. проверьте, что наследники класса Task равны друг другу, если равен их id;
    @Test
    public void subtasksWithSameIdEqualTest() {
        Task taskOne = new Subtask(id,"Name", "desc", Status.NEW, 2,
                LocalDateTime.of(2025, 7, 15, 18, 30), Duration.of(70, ChronoUnit.MINUTES));
        Task taskTwo = new Subtask(id,"NameTwo", "description", Status.DONE, 3,
                LocalDateTime.of(2025, 7, 15, 18, 30), Duration.of(70, ChronoUnit.MINUTES));
        Assertions.assertEquals(taskOne, taskTwo, "Подзадачи с одинаковым ID не равны");
    }

//   4. проверьте, что объект Subtask нельзя сделать своим же эпиком
    @Test
    public void subtaskShouldNotBeAddAsEpicToItselfTest() throws Exception {
        TaskManager taskManager = Managers.getDefault();
        taskManager.createTask(new Subtask(id,"task", "desc", Status.NEW, id,
                LocalDateTime.of(2025, 7, 15, 18, 30), Duration.of(70, ChronoUnit.MINUTES)));
        Assertions.assertTrue(taskManager.getTasks().size() == 0, "Подзадача была создана");
        System.out.println(LocalDateTime.of(2025, 7, 15, 18, 30));
        System.out.println(Duration.of(70, ChronoUnit.MINUTES));
    }
}
