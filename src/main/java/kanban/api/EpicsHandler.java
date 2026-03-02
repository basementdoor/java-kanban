package kanban.api;

import kanban.manager.TaskManager;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager manager) {
        super(manager);
    }
}
