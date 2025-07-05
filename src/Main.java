import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import static util.Status.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager taskManager = Managers.getDefault();

        System.out.println("Создание задач разного типа:");

        Task simpleTask = new Task("First task", "Reaaallyy important task", NEW);
        taskManager.createTask(simpleTask);
        taskManager.getTaskById(simpleTask.getId());
        System.out.println(taskManager.getHistory());

        Epic oneTaskEpic = new Epic("First epic", "Contains one task");
        taskManager.createTask(oneTaskEpic);
        taskManager.getTaskById(oneTaskEpic.getId());
        System.out.println(taskManager.getHistory());

        Subtask subtaskFirstEpic = new Subtask("Think", "For first epic", NEW, oneTaskEpic.getId());
        taskManager.createTask(subtaskFirstEpic);
        taskManager.getTaskById(subtaskFirstEpic.getId());
        System.out.println(taskManager.getHistory());
    }
}
