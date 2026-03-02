package kanban.exception;

public class TaskIntersectionException extends  RuntimeException {

    public TaskIntersectionException(String message) {
        super(message);
    }
}
