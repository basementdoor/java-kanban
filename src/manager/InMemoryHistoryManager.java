package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (history.size() == 10) {
            history.remove(0);
        }
        // далее реализовано добавление копий тасок, чтобы хранить состояние на момент добавления
        if (task instanceof Task) {
            history.add(new Task(task.getId(), task.getName(), task.getDescription(), task.getTaskStatus()));
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            history.add(new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(),
                    subtask.getTaskStatus(), subtask.getEpicId()));
        } else {
            Epic epic = (Epic) task;
            history.add(new Epic(epic.getId(), epic.getName(), epic.getDescription()));
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return history;
    }
}
