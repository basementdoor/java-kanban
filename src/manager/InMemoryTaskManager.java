package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import util.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    protected static int uniqueId = 1;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void createTask(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            createSubtask(subtask);
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            createEpic(epic);
        } else {
            task.setId(generateId());
            tasks.put(task.getId(), task);
            System.out.println("Задача %s успешно создана".formatted(task));
        }
    }

    private void createSubtask(Subtask subtask) {
        if (tasks.containsKey(subtask.getId())) {
            System.out.println("Подзадача с ID %s уже существует".formatted(subtask.getId()));
        } else if (!tasks.containsKey(subtask.getEpicId())) {
            System.out.println("Эпик с ID %s не найден. Укажите корректный ID или создайте новый эпик"
                    .formatted(subtask.getEpicId()));
        } else if (subtask.getId() == subtask.getEpicId()) {
            System.out.println("Подзадача не может быть собственным эпиком");
        } else {
            subtask.setId(generateId());
            tasks.put(subtask.getId(), subtask);
            var epic = (Epic) tasks.get(subtask.getEpicId());
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic);
            System.out.println("Подзадача %s успешно создана".formatted(subtask));
        }
    }

    private void createEpic(Epic epic) {
        if (tasks.containsKey(epic.getId())) {
            System.out.println("Эпик с ID %s уже существует".formatted(epic.getId()));
        } else {
            epic.setId(generateId());
            tasks.put(epic.getId(), epic);
            System.out.println("Эпик %s успешно создан".formatted(epic));
        }
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Задача с ID %s не найдена".formatted(task.getId()));
        } else {
            if (task instanceof Subtask) {
                updateSubtask((Subtask) task);
            } else if (task instanceof Epic) {
                updateEpic((Epic) task);
            } else {
                tasks.put(task.getId(), task);
                System.out.println("Задача %s успешно обновлена".formatted(task));
            }
        }
    }

    private void updateSubtask(Subtask subtask) {
        if (!tasks.containsKey(subtask.getEpicId())) {
            System.out.println("Эпик с ID %s не найден".formatted(subtask.getEpicId()));
        } else {
            tasks.put(subtask.getId(), subtask);
            var epic = (Epic) tasks.get(subtask.getEpicId());
            updateEpicStatus(epic);
            System.out.println("Задача %s успешно обновлена".formatted(subtask));
        }
    }

    private void updateEpic(Epic epic) {
        var epicBeforeUpdate = (Epic) tasks.get(epic.getId());
        epicBeforeUpdate.setDescription(epic.getDescription());
        epicBeforeUpdate.setName(epic.getName());
        System.out.println("Эпик %s успешно обновлен".formatted(epic));
    }

    @Override
    public Task getTaskById(int id) {
        var task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            System.out.println("Не найдена задача с ID " + id);
        } else {
            Task task = tasks.get(id);
            if (task instanceof Subtask) {
                deleteSubtaskById(id);
            } else if (task instanceof Epic) {
                deleteEpicById(id);
            } else {
                tasks.remove(id);
                historyManager.remove(id);
                System.out.println("Задача с ID %s успешно удалена".formatted(id));
            }
        }
    }

    private void deleteSubtaskById(int id) {
        Subtask subtask = (Subtask) tasks.get(id);
        var epic = (Epic) tasks.get(subtask.getEpicId());
        tasks.remove(id);
        epic.getSubtasksId().remove(Integer.valueOf(id));
        updateEpicStatus(epic);
        historyManager.remove(id);
        System.out.println("Подзадача с ID %s успешно удалена".formatted(id));
    }

    private void deleteEpicById(int id) {
        var epic = (Epic) tasks.get(id);
        for (Integer subtaskId: epic.getSubtasksId()) {
            tasks.remove(subtaskId);
        }
        tasks.remove(id);
        historyManager.remove(id);
        System.out.println("Эпик с ID %s успешно удален".formatted(id));
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int generateId() {
        while (tasks.containsKey(uniqueId)) {
            uniqueId++;
        }
        return uniqueId++;
    }

    private void updateEpicStatus(Epic epic) {
        var isAllNew = true;
        var isAllDone = true;
        if (epic.getSubtasksId().size() != 0) {
            for (Integer subtaskId : epic.getSubtasksId()) {
                var subtask = (Subtask) tasks.get(subtaskId);
                if (!(subtask.getTaskStatus() == Status.NEW)) isAllNew = false;
                else if (!(subtask.getTaskStatus() == Status.DONE)) isAllDone = false;
            }

        }

        if (isAllNew) epic.setTaskStatus(Status.NEW);
        else if (isAllDone) epic.setTaskStatus(Status.DONE);
        else epic.setTaskStatus(Status.IN_PROGRESS);
    }
}
