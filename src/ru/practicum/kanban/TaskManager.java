package ru.practicum.kanban;

import java.util.HashMap;

public class TaskManager {

    public static int uniqueId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public void deleteAllTasks() {
        tasks.clear();
        subtasks.clear();
        epics.clear();
    }

    public void getSimpleTasks() {
        for (Task task : tasks.values()) {
            System.out.println(task);
        }
    }

    public void getSubtasks() {
        for (Task task : subtasks.values()) {
            System.out.println(task);
        }
    }

    public void getEpics() {
        for (Task epic : epics.values()) {
            System.out.println(epic);
        }
    }

    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void createSubtask(Subtask subtask) {
        tasks.put(subtask.getId(), subtask);
    }

    public void createEpic(Epic epic) {
        tasks.put(epic.getId(), epic);
    }


}
