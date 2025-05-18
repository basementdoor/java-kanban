package ru.practicum.kanban;

import java.util.Objects;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, TaskStatus taskStatus, int epicId) {
        super(name, description, taskStatus);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, TaskStatus taskStatus, int epicId) {
        super(id, name, description, taskStatus);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Subtask subtask = (Subtask) obj;
        return epicId == subtask.epicId && this.getId() == subtask.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
