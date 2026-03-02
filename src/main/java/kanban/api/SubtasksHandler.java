package kanban.api;

import kanban.manager.TaskManager;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager manager) {
        super(manager);
    }
}
