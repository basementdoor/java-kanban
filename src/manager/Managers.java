package manager;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        File file = new File("resources/data.csv");
        file.getParentFile().mkdirs();
        return new FileBackedTaskManager(file);
    }

    public static TaskManager getInMemory() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
