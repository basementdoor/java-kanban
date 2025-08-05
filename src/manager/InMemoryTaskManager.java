package manager;

import exception.TaskIntersectionException;
import model.Epic;
import model.Subtask;
import model.Task;
import util.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected int uniqueId = 1;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));

    @Override
    public void createTask(Task task) {
        if (hasIntersection(task)) {
            throw new TaskIntersectionException("Время выполнения создаваемой задачи пересекается с ранее созданными");
        }
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            createSubtask(subtask);
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            createEpic(epic);
        } else {
            task.setId(generateId());
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
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
            prioritizedTasks.add(subtask);
            var epic = (Epic) tasks.get(subtask.getEpicId());
            epic.addSubtask(subtask.getId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
            System.out.println("Подзадача %s успешно создана".formatted(subtask));
        }
    }

    private void createEpic(Epic epic) {
        if (tasks.containsKey(epic.getId())) {
            System.out.println("Эпик с ID %s уже существует".formatted(epic.getId()));
        } else {
            epic.setId(generateId());
            tasks.put(epic.getId(), epic);
            prioritizedTasks.add(epic);
            System.out.println("Эпик %s успешно создан".formatted(epic));
        }
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Задача с ID %s не найдена".formatted(task.getId()));
        } else if (hasIntersection(task)) {
            throw new TaskIntersectionException("Время выполнения обновленной задачи пересекается с ранее созданными");
        } else {
            if (task instanceof Subtask) {
                updateSubtask((Subtask) task);
            } else if (task instanceof Epic) {
                updateEpic((Epic) task);
            } else {
                tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
                System.out.println("Задача %s успешно обновлена".formatted(task));
            }
        }
    }

    private void updateSubtask(Subtask subtask) {
        if (!tasks.containsKey(subtask.getEpicId())) {
            System.out.println("Эпик с ID %s не найден".formatted(subtask.getEpicId()));
        } else {
            tasks.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            var epic = (Epic) tasks.get(subtask.getEpicId());
            updateEpicStatus(epic);
            updateEpicTime(epic);
            System.out.println("Задача %s успешно обновлена".formatted(subtask));
        }
    }

    private void updateEpic(Epic epic) {
        var epicBeforeUpdate = (Epic) tasks.get(epic.getId());
        epicBeforeUpdate.setDescription(epic.getDescription());
        epicBeforeUpdate.setName(epic.getName());
        prioritizedTasks.add(epic);
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
                prioritizedTasks.remove(task);
                historyManager.remove(id);
                System.out.println("Задача с ID %s успешно удалена".formatted(id));
            }
        }
    }

    private void deleteSubtaskById(int id) {
        Subtask subtask = (Subtask) tasks.get(id);
        var epic = (Epic) tasks.get(subtask.getEpicId());
        tasks.remove(id);
        prioritizedTasks.remove(subtask);
        epic.getSubtasksId().remove(Integer.valueOf(id));
        updateEpicStatus(epic);
        updateEpicTime(epic);
        historyManager.remove(id);
        System.out.println("Подзадача с ID %s успешно удалена".formatted(id));
    }

    private void deleteEpicById(int id) {
        var epic = (Epic) tasks.get(id);
        epic.getSubtasksId().forEach(subtaskId -> {
            tasks.remove(subtaskId);
            try {
                prioritizedTasks.remove(subtaskId);
            } catch (Exception e) {
                e.getMessage();
            }
        });
        tasks.remove(id);
        prioritizedTasks.remove(epic);
        historyManager.remove(id);
        System.out.println("Эпик с ID %s успешно удален".formatted(id));
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private boolean isIntersection(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getEndTime() == null
                || task2.getStartTime() == null || task2.getEndTime() == null) {
            return false;
        }

        if (task1.getStartTime().isAfter(task2.getStartTime())) {
            return task2.getEndTime().isAfter(task1.getStartTime());
        } else if (task1.getStartTime().isBefore(task2.getStartTime())) {
            return task1.getEndTime().isAfter(task2.getStartTime());
        } else return true;
    }

    private boolean hasIntersection(Task task) {
        return prioritizedTasks.stream()
                .filter(t -> t.getId() != task.getId())
                .filter(t -> t.getStartTime() != null && t.getEndTime() != null)
                .anyMatch(t -> isIntersection(t, task));
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
                if (subtask.getTaskStatus() != Status.NEW) isAllNew = false;
                if (subtask.getTaskStatus() != Status.DONE) isAllDone = false;
            }
        }

        if (isAllNew) epic.setTaskStatus(Status.NEW);
        else if (isAllDone) epic.setTaskStatus(Status.DONE);
        else epic.setTaskStatus(Status.IN_PROGRESS);
    }

    private void updateEpicTime(Epic epic) {
        if (!epic.getSubtasksId().isEmpty()) {
            var earliestStart = epic.getSubtasksId().stream()
                    .map(tasks::get)
                    .filter(Objects::nonNull)
                    .map(Task::getStartTime)
                    .filter(Objects::nonNull)
                    .min(Comparator.naturalOrder());

            var duration = epic.getSubtasksId().stream()
                    .map(tasks::get)
                    .filter(Objects::nonNull)
                    .map(Task::getDuration)
                    .filter(Objects::nonNull)
                    .reduce(Duration.ZERO, Duration::plus);

            var latestEnd = epic.getSubtasksId().stream()
                    .map(tasks::get)
                    .filter(Objects::nonNull)
                    .map(Task::getEndTime)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo);

            epic.setStartTime(earliestStart.get());
            epic.setDuration(duration);
            epic.setEndTime(latestEnd.get());
        }
    }
}
