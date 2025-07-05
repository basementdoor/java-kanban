package util;

import manager.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public class CSVFormatter {

    public static String getHeader() {
        return "id,type,name,status,description,epic";
    }

    public static String taskToString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%d", subtask.getId(), TaskType.SUBTASK, subtask.getName(),
                    subtask.getTaskStatus(), subtask.getDescription(), subtask.getEpicId());
        } else {
            var taskType = task instanceof Epic ? TaskType.EPIC : TaskType.TASK;
            return String.format("%d,%s,%s,%s,%s", task.getId(), taskType, task.getName(), task.getTaskStatus(),
                    task.getDescription());
        }
    }

    public static Task taskFromString(String value) {
        String[] taskFields = value.split(",");
        int id = Integer.parseInt(taskFields[0]);
        TaskType type = TaskType.valueOf(taskFields[1]);
        String name = taskFields[2];
        Status status = Status.valueOf(taskFields[3]);
        String desc = taskFields[4];
        if (type == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(taskFields[5]);
            return new Subtask(id, name, desc, status, epicId);
        } else if (type == TaskType.EPIC) {
            return new Epic(id, name, desc);
        } else {
            return new Task(id, name, desc, status);
        }
    }

    public static String historyToString(HistoryManager historyManager) {
        List<Task> history = historyManager.getHistory();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            builder.append(history.get(i).getId());
            if (i < history.size() - 1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    public static String[] historyFromString(String value) {
        return value.split(",");
    }
}
