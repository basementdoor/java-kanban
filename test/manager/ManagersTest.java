package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {

//  5. убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров
    @Test
    public void managersInitTest() {
        Assertions.assertEquals(FileBackedTaskManager.class, Managers.getDefault().getClass(),
                "Некорректная инициализация");
        Assertions.assertEquals(InMemoryHistoryManager.class, Managers.getDefaultHistory().getClass(),
                "Некорректная инициализация");
    }
}
