package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import util.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    public static int uniqueId = 1;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        System.out.println("Задача %s успешно создана".formatted(task));
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            System.out.println("Подзадача с ID %s уже существует".formatted(subtask.getId()));
        } else if (!epics.containsKey(subtask.getEpicId())) {
            System.out.println("Эпик с ID %s не найден. Укажите корректный ID или создайте новый эпик"
                    .formatted(subtask.getEpicId()));
        } else if (subtask.getId() == subtask.getEpicId()) {
            System.out.println("Подзадача не может быть собственным эпиком");
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
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        var subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        var epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Subtask>  getSubtasks() {
        return subtasks;
    }

    @Override
    public HashMap<Integer, Epic>  getEpics() {
        return epics;
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
            subtasks.remove(Id);
            epic.getSubtasksId().remove(Integer.valueOf(Id));
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
        return historyManager.getHistory();
    }

    private int generateId() {
        while (tasks.containsKey(uniqueId) || subtasks.containsKey(uniqueId) || epics.containsKey(uniqueId)) {
            uniqueId++;
        }
        return uniqueId++;
    }

    private void updateEpicStatus(Epic epic) {
        var isAllNew = true;
        var isAllDone = true;
        if (epic.getSubtasksId().size() != 0) {
            for (Integer subtaskId : epic.getSubtasksId()) {
                var subtask = subtasks.get(subtaskId);
                if (!(subtask.getTaskStatus() == Status.NEW)) isAllNew = false;
                else if (!(subtask.getTaskStatus() == Status.DONE)) isAllDone = false;
            }

        }

        if (isAllNew) epic.setTaskStatus(Status.NEW);
        else if (isAllDone) epic.setTaskStatus(Status.DONE);
        else epic.setTaskStatus(Status.IN_PROGRESS);
    }
}
