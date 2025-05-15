import ru.practicum.kanban.Task;
import ru.practicum.kanban.TaskManager;
import ru.practicum.kanban.TaskStatus;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

        Task testTask = new Task("First task", "Reaaallyy important task", TaskStatus.NEW);
        taskManager.createTask(testTask);
        System.out.println(testTask);
    }
}
