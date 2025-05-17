import ru.practicum.kanban.*;

import static ru.practicum.kanban.TaskStatus.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();

//        Тесты
        System.out.println("1. Создание двух простых задач, эпика с одной подзадачей, и эпика с двумя подзадачами");

        Task firstSimpleTask = new Task("First task", "Reaaallyy important task", NEW);
        taskManager.createTask(firstSimpleTask);
        System.out.println();

        Task secondSimpleTask = new Task("Second task", "Please, do something", IN_PROGRESS);
        taskManager.createTask(secondSimpleTask);
        System.out.println();

        Epic oneTaskEpic = new Epic("First epic", "Contains one task");
        taskManager.createEpic(oneTaskEpic);
        System.out.println();

        Subtask subtaskFirstEpic = new Subtask("Think", "For first epic", NEW, oneTaskEpic.getId());
        taskManager.createSubtask(subtaskFirstEpic);
        System.out.println();

        Epic twoTaskEpic = new Epic("Second epic", "Contains two tasks");
        taskManager.createEpic(twoTaskEpic);
        System.out.println();

        Subtask subtaskSecondEpicOne = new Subtask("Do", "For second epic", IN_PROGRESS, twoTaskEpic.getId());
        taskManager.createSubtask(subtaskSecondEpicOne);
        System.out.println();

        Subtask subtaskSecondEpicTwo = new Subtask("Repeat", "For second epic", NEW, twoTaskEpic.getId());
        taskManager.createSubtask(subtaskSecondEpicTwo);
        System.out.println();

        System.out.println("2. Выведение списков задач");

        printAllTasks(taskManager);

        System.out.println("3. Изменение созданных объектов");

        Task taskForUpdate = new Task(firstSimpleTask.getId(), "New name", "New description", IN_PROGRESS);
        taskManager.updateTask(taskForUpdate);
        System.out.println();

        Epic epicForUpdate = new Epic(oneTaskEpic.getId(), "New epic name", "Updated description");
        taskManager.updateEpic(epicForUpdate);
        System.out.println();

        Subtask subtaskForUpdate = new Subtask(subtaskFirstEpic.getId(), "New subtask name", "Think twice", DONE,
                subtaskFirstEpic.getEpicId());
        taskManager.updateSubtask(subtaskForUpdate);
        System.out.println();

        printAllTasks(taskManager);

        System.out.println("4. Удаление объектов");

        taskManager.deleteTaskById(firstSimpleTask.getId());
        System.out.println();

        taskManager.deleteEpicById(twoTaskEpic.getId());
        System.out.println();

        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager taskManager) {
        taskManager.getTasks();
        System.out.println();
        taskManager.getSubtasks();
        System.out.println();
        taskManager.getEpics();
        System.out.println();
    }
}
