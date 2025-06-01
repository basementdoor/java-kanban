package model;

import util.Status;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description, Status.NEW);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW);
        this.subtasksId = new ArrayList<>();
    }

    public void addSubtask(int subtaskId) {
        if (this.getId() == subtaskId) {
            return;
        }
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
        return this.getId() == epic.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksId);
    }
}
