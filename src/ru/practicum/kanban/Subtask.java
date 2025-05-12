package ru.practicum.kanban;

public class Subtask extends Task {

    private final Epic epic;

    public Subtask(int id, String name, String description, Epic epic) {
        super(id, name, description);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}
