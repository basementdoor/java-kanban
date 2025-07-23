package manager;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public interface TaskManager {
    void createTask(Task task);

    void updateTask(Task task);

    Task getTaskById(int id);

    HashMap<Integer, Task> getTasks();

    void deleteTaskById(int id);

    void deleteAllTasks();

    ArrayList<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();
}
