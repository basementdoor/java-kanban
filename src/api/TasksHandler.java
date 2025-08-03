package api;

import manager.TaskManager;

public class TasksHandler extends  BaseHttpHandler{

    public TasksHandler(TaskManager manager) {
        super(manager);
    }
}
