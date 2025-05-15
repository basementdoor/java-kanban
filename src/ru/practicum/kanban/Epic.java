package ru.practicum.kanban;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void updateStatus() {
        var isAllNew = true;
        var isAllDone = true;
        for (Subtask subtask : subtasks) {
            if (!(subtask.getTaskStatus() == TaskStatus.NEW)) isAllNew = false;
            else if (!(subtask.getTaskStatus() == TaskStatus.DONE)) isAllDone = false;
        }

        if (isAllNew) setTaskStatus(TaskStatus.NEW);
        else if (isAllDone) setTaskStatus(TaskStatus.DONE);
        else setTaskStatus(TaskStatus.IN_PROGRESS);
    }


}
