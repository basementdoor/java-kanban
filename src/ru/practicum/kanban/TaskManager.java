package ru.practicum.kanban;

import java.util.HashMap;

public class TaskManager {

    public static int uniqueId = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public void createTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            System.out.println("Задача с ID %s уже существует".formatted(task.getId()));
        } else {
            task.setId(generateId());
            tasks.put(task.getId(), task);
            System.out.println("Задача %s успешно создана".formatted(task));
        }
    }

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
            epic.addSubtask(subtask);
            epic.updateStatus();
            System.out.println("Подзадача %s успешно создана".formatted(subtask));
        }
    }

    public void createEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            System.out.println("Эпик с ID %s уже существует".formatted(epic.getId()));
        } else {
            epic.setId(generateId());
            epics.put(epic.getId(), epic);
            System.out.println("Эпик %s успешно создан".formatted(epic));
        }
    }

    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Задача с ID %s не найдена".formatted(task.getId()));
        } else {
            tasks.put(task.getId(), task);
            System.out.println("Задача %s успешно обновлена".formatted(task));
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            System.out.println("Подзадача с ID %s не найдена".formatted(subtask.getId()));
        } else if (!epics.containsKey(subtask.getEpicId())) {
            System.out.println("Эпик с ID %s не найден".formatted(subtask.getEpicId()));
        } else {
            subtasks.put(subtask.getId(), subtask);
            var epic = epics.get(subtask.getEpicId());
            epic.updateStatus();
            System.out.println("Задача %s успешно обновлена".formatted(subtask));
        }
    }

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

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void getTasks() {
        System.out.println("Список задач:");
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
    }

    public void getSubtasks() {
        System.out.println("Список подзадач:");
        for (Task task : subtasks.values()) {
            System.out.println(task);
        }
    }

    public void getEpics() {
        System.out.println("Список эпиков:");
        for (Task epic : epics.values()) {
            System.out.println(epic);
        }
    }

    public void deleteTaskById(int Id) {
        if (!tasks.containsKey(Id)) {
            System.out.println("Не найдена задача с ID " + Id);
        }
        tasks.remove(Id);
        System.out.println("Задача с ID %s успешно удалена".formatted(Id));
    }

    public void deleteSubtaskById(int Id) {
        if (!subtasks.containsKey(Id)) {
            System.out.println("Не найдена подзадача с ID " + Id);
        } else {
            var epic = epics.get(subtasks.get(Id).getEpicId());
            var subtask = subtasks.get(Id);
            subtasks.remove(Id);
            epic.getSubtasks().remove(subtask);
            epic.updateStatus();
            System.out.println("Подзадача с ID %s успешно удалена".formatted(Id));
        }

    }

    public void deleteEpicById(int Id) {
        if (!epics.containsKey(Id)) {
            System.out.println("Не найден эпик с ID " + Id);
        } else {
            var epic = epics.get(Id);
            for (Subtask subtask: epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
            epics.remove(Id);
            System.out.println("Эпик с ID %s успешно удален".formatted(Id));
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    private int generateId() {
        return uniqueId++;
    }
}
