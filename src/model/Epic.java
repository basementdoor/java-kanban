package model;

import util.TaskStatus;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
        this.subtasksId = new ArrayList<>();
    }

    public void addSubtask(int subtaskId) {
        subtasksId.add(subtaskId);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Epic epic = (Epic) obj;
        return Objects.equals(subtasksId, epic.subtasksId) && this.getId() == epic.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksId);
    }
}
