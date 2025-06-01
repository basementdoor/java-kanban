package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Status;

public class TaskTest {

//    1. проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    public void tasksWithSameIdEqualTest() {
        int id = 1;
        Task taskOne = new Task(id,"Name", "desc", Status.NEW);
        Task taskTwo = new Task(id,"NameTwo", "description", Status.DONE);
        Assertions.assertEquals(taskOne, taskTwo, "Задачи с одинаковым ID не равны");
    }
}
