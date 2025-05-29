package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import util.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    public static int uniqueId = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void createTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            System.out.println("Задача с ID %s уже существует".formatted(task.getId()));
        } else {
            task.setId(generateId());
            tasks.put(task.getId(), task);
            System.out.println("Задача %s успешно создана".formatted(task));
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            System.out.println("Подзадача с ID %s уже существует".formatted(subtask.getId()));
        } else if (!epics.containsKey(subtask.getEpicId())) {
            System.out.println("Эпик с ID %s не найден. Укажите корректный ID или создайте новый эпик"
                    .formatted(subtask.getEpicId()));
        } else {
            subtask.setId(generateId());
            subtasks.put(subtask.getId(), subtask);
            var epic = epics.get(subtask.getEpicId());
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic);
            System.out.println("Подзадача %s успешно создана".formatted(subtask));
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            System.out.println("Эпик с ID %s уже существует".formatted(epic.getId()));
        } else {
            epic.setId(generateId());
            epics.put(epic.getId(), epic);
            System.out.println("Эпик %s успешно создан".formatted(epic));
        }
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Задача с ID %s не найдена".formatted(task.getId()));
        } else {
            tasks.put(task.getId(), task);
            System.out.println("Задача %s успешно обновлена".formatted(task));
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            System.out.println("Подзадача с ID %s не найдена".formatted(subtask.getId()));
        } else if (!epics.containsKey(subtask.getEpicId())) {
            System.out.println("Эпик с ID %s не найден".formatted(subtask.getEpicId()));
        } else {
            subtasks.put(subtask.getId(), subtask);
            var epic = epics.get(subtask.getEpicId());
            updateEpicStatus(epic);
            System.out.println("Задача %s успешно обновлена".formatted(subtask));
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Эпик с ID %s не найден".formatted(epic.getId()));
        } else {
            var epicBeforeUpdate = epics.get(epic.getId());
            epicBeforeUpdate.setDescription(epic.getDescription());
            epicBeforeUpdate.setName(epic.getName());
            System.out.println("Эпик %s успешно обновлен".formatted(epic));
        }
    }

    @Override
    public Task getTaskById(int id) {
        var task = tasks.get(id);
        addTaskToHistory(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        var subtask = subtasks.get(id);
        addTaskToHistory(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        var epic = epics.get(id);
        addTaskToHistory(epic);
        return epic;
    }

    @Override
    public void getTasks() {
        System.out.println("Список задач:");
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
    }

    @Override
    public void getSubtasks() {
        System.out.println("Список подзадач:");
        for (Task task : subtasks.values()) {
            System.out.println(task);
        }
    }

    @Override
    public void getEpics() {
        System.out.println("Список эпиков:");
        for (Task epic : epics.values()) {
            System.out.println(epic);
        }
    }

    @Override
    public void deleteTaskById(int Id) {
        if (!tasks.containsKey(Id)) {
            System.out.println("Не найдена задача с ID " + Id);
        }
        tasks.remove(Id);
        System.out.println("Задача с ID %s успешно удалена".formatted(Id));
    }

    @Override
    public void deleteSubtaskById(int Id) {
        if (!subtasks.containsKey(Id)) {
            System.out.println("Не найдена подзадача с ID " + Id);
        } else {
            var epic = epics.get(subtasks.get(Id).getEpicId());
            var subtask = subtasks.get(Id);
            subtasks.remove(Id);
            epic.getSubtasksId().remove(subtask);
            updateEpicStatus(epic);
            System.out.println("Подзадача с ID %s успешно удалена".formatted(Id));
        }

    }

    @Override
    public void deleteEpicById(int Id) {
        if (!epics.containsKey(Id)) {
            System.out.println("Не найден эпик с ID " + Id);
        } else {
            var epic = epics.get(Id);
            for (Integer subtaskId: epic.getSubtasksId()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(Id);
            System.out.println("Эпик с ID %s успешно удален".formatted(Id));
        }
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }

    private void addTaskToHistory(Task task) {
        if (history.size() == 10) {
            history.remove(0);
        }
        history.add(task);
    }

    private int generateId() {
        return uniqueId++;
    }

    private void updateEpicStatus(Epic epic) {
        var isAllNew = true;
        var isAllDone = true;
        for (Integer subtaskId : epic.getSubtasksId()) {
            var subtask = subtasks.get(subtaskId);
            if (!(subtask.getTaskStatus() == TaskStatus.NEW)) isAllNew = false;
            else if (!(subtask.getTaskStatus() == TaskStatus.DONE)) isAllDone = false;
        }

        if (isAllNew) epic.setTaskStatus(TaskStatus.NEW);
        else if (isAllDone) epic.setTaskStatus(TaskStatus.DONE);
        else epic.setTaskStatus(TaskStatus.IN_PROGRESS);
    }
}
