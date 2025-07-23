package model;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class EpicTest {

    private TaskManager taskManager;

    @BeforeEach
    public void managerSetup() {
        taskManager = Managers.getDefault();
    }

//   2. проверьте, что наследники класса Task равны друг другу, если равен их id;
    @Test
    public void epicsWithSameIdEqualTest() {
        int id = 1;
        Task epicOne = new Epic(id,"Name", "desc");
        Task epicTwo = new Epic(id,"NameTwo", "description");
        Assertions.assertEquals(epicOne, epicTwo, "Эпики с одинаковым ID не равны");
    }

//   3. проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи
    @Test
    public void epicShouldNotBeAddAsSubtaskToItselfTest() {
        Epic testEpic = new Epic(1,"Name", "desc");
        testEpic.addSubtask(testEpic.getId());
        Assertions.assertFalse(testEpic.getSubtasksId().contains(testEpic.getId()),
                "Эпик был добавлен сам в себя в качестве подзадачи");
    }

    @Test
    public void newEpicStatusShouldBeNewTest() {
        Epic testEpic = new Epic(1, "Name", "desc");
        taskManager.createTask(testEpic);
        Assertions.assertEquals(testEpic.getTaskStatus(), Status.NEW, "Статус не NEW");
    }

    @Test
    public void epicWithAllDoneTasksShouldBeDoneTest() {
        Epic testEpic = new Epic(1, "Name", "desc");
        taskManager.createTask(testEpic);
        Subtask subtask1 = new Subtask("Name", "desc", Status.DONE, testEpic.getId(),
                LocalDateTime.now(), Duration.of(70, ChronoUnit.MINUTES));
        Subtask subtask2 = new Subtask("NameTwo", "description", Status.DONE, testEpic.getId(),
                LocalDateTime.of(2025, 7, 15, 18, 30), Duration.of(70, ChronoUnit.MINUTES));
        taskManager.createTask(subtask1);
        taskManager.createTask(subtask2);
        Assertions.assertEquals(testEpic.getTaskStatus(), Status.DONE, "Статус не DONE");
    }

    @Test
    public void epicWithDoneAndInProgressTasksShouldBeInProgressTest() {
        Epic testEpic = new Epic("Name", "desc");
        taskManager.createTask(testEpic);
        Subtask subtask1 = new Subtask("Name", "desc", Status.DONE, testEpic.getId(),
                LocalDateTime.now(), Duration.of(70, ChronoUnit.MINUTES));
        Subtask subtask2 = new Subtask("NameTwo", "description", Status.IN_PROGRESS, testEpic.getId(),
                LocalDateTime.of(2025, 7, 15, 18, 30), Duration.of(70, ChronoUnit.MINUTES));
        taskManager.createTask(subtask1);
        taskManager.createTask(subtask2);
        Assertions.assertEquals(testEpic.getTaskStatus(), Status.IN_PROGRESS, "Статус не IN_PROGRESS");
    }

    @Test
    public void epicWithAllInProgressTasksShouldBeInProgressTest() {
        Epic testEpic = new Epic("Name", "desc");
        taskManager.createTask(testEpic);
        Subtask subtask1 = new Subtask("Name", "desc", Status.IN_PROGRESS, testEpic.getId(),
                LocalDateTime.now(), Duration.of(70, ChronoUnit.MINUTES));
        Subtask subtask2 = new Subtask("NameTwo", "description", Status.IN_PROGRESS, testEpic.getId(),
                LocalDateTime.of(2025, 7, 15, 18, 30), Duration.of(70, ChronoUnit.MINUTES));
        taskManager.createTask(subtask1);
        taskManager.createTask(subtask2);
        Assertions.assertEquals(testEpic.getTaskStatus(), Status.IN_PROGRESS, "Статус не IN_PROGRESS");
    }

}
