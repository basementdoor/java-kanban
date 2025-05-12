package ru.practicum.kanban;

import java.util.ArrayList;

public class Epic extends Task {

    protected ArrayList<Subtask> subtasks;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }


}
