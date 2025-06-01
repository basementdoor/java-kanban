package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EpicTest {

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

}
