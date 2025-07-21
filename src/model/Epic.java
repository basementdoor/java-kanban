package model;

import util.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, Status.NEW, null, null);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description, Status.NEW, null, null);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(int id, String name, String description, LocalDateTime startTime, Duration duration,
                LocalDateTime endTime) {
        this(id, name, description);
        setStartTime(startTime);
        setDuration(duration);
        setEndTime(endTime);
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
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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
