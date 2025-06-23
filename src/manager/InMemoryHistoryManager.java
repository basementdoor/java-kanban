package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history = new ArrayList<>();
    final Map<Integer, Node> nodes = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        removeNode(task.getId());
        linkLast(task);
        nodes.put(task.getId(), last);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node node = nodes.remove(id);
        if (node != null) {
            removeNode(id);
        }
    }

    private void removeNode(int id) {
        Node node = nodes.get(id);
        if (node == null) {
            return;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            first = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            last = node.prev;
        }
    }

    private void linkLast(Task task) {
        Task taskCopy;
        if (task instanceof Task) {
            taskCopy = new Task(task.getId(), task.getName(), task.getDescription(), task.getTaskStatus());
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            taskCopy = new Subtask(subtask.getId(), subtask.getName(), subtask.getDescription(),
                    subtask.getTaskStatus(), subtask.getEpicId());
        } else {
            Epic epic = (Epic) task;
            taskCopy = new Epic(epic.getId(), epic.getName(), epic.getDescription());
        }

        Node node = new Node(taskCopy, last, null);
        if (first == null) {
            first = node;
        } else {
            last.next = node;
        }
        last = node;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Node node : nodes.values()) {
            tasks.add(node.value);
        }
        return tasks;
    }

    private class Node {
        public Task value;
        public Node prev;
        public Node next;

        public Node(Task value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }
    }
}
