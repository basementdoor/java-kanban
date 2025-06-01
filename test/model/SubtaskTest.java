package model;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Status;

public class SubtaskTest {

    private final int id = 1;

//   2. проверьте, что наследники класса Task равны друг другу, если равен их id;
    @Test
    public void subtasksWithSameIdEqualTest() {
        Task taskOne = new Subtask(id,"Name", "desc", Status.NEW, 2);
        Task taskTwo = new Subtask(id,"NameTwo", "description", Status.DONE, 3);
        Assertions.assertEquals(taskOne, taskTwo, "Подзадачи с одинаковым ID не равны");
    }

//   4. проверьте, что объект Subtask нельзя сделать своим же эпиком
    @Test
    public void subtaskShouldNotBeAddAsEpicToItselfTest() throws Exception {
        TaskManager taskManager = Managers.getDefault();
        taskManager.createSubtask(new Subtask(id,"task", "desc", Status.NEW, id));
        Assertions.assertTrue(taskManager.getSubtasks().size() == 0, "Подзадача была создана");
    }
}
