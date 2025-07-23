package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TaskTest {

//    1. проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    public void tasksWithSameIdEqualTest() {
        int id = 1;
        Task taskOne = new Task(id,"Name", "desc", Status.NEW, LocalDateTime.now(),
                Duration.of(1, ChronoUnit.MINUTES));
        Task taskTwo = new Task(id,"NameTwo", "description", Status.DONE, LocalDateTime.now().minusDays(1),
                Duration.of(1, ChronoUnit.MINUTES));
        Assertions.assertEquals(taskOne, taskTwo, "Задачи с одинаковым ID не равны");
    }
}
