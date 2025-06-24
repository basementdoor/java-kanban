package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import util.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> nodes = new HashMap<>();
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

        if (node.getPrev() != null) {
            var prevNode = node.getPrev();
            prevNode.setNext(node.getNext());
        } else {
            first = node.getNext();
        }

        if (node.getNext() != null) {
            var nextNode = node.getNext();
            nextNode.setPrev(node.getPrev());
        } else {
            last = node.getPrev();
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
            last.setNext(node);
        }
        last = node;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Node node : nodes.values()) {
            tasks.add(node.getValue());
        }
        return tasks;
    }
}
