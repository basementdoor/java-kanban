package util;

import manager.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CSVFormatter {

    public static String getHeader() {
        return "id,type,name,status,description,startTime,duration,endTime,epic";
    }

    public static String taskToString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%d", subtask.getId(), TaskType.SUBTASK, subtask.getName(),
                    subtask.getTaskStatus(), subtask.getDescription(), subtask.getStartTime(), subtask.getDuration(),
                    subtask.getEndTime(), subtask.getEpicId());
        } else {
            var taskType = task instanceof Epic ? TaskType.EPIC : TaskType.TASK;
            return String.format("%d,%s,%s,%s,%s,%s,%s,%s", task.getId(), taskType, task.getName(), task.getTaskStatus(),
                    task.getDescription(), task.getStartTime(), task.getDuration(), task.getEndTime());
        }
    }

    public static Task taskFromString(String value) {
        String[] taskFields = value.split(",");
        int id = Integer.parseInt(taskFields[0]);
        TaskType type = TaskType.valueOf(taskFields[1]);
        String name = taskFields[2];
        Status status = Status.valueOf(taskFields[3]);
        String desc = taskFields[4];
        LocalDateTime startTime = LocalDateTime.parse(taskFields[5]);
        Duration duration = Duration.parse(taskFields[6]);
        LocalDateTime endTime = LocalDateTime.parse(taskFields[7]);
        if (type == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(taskFields[8]);
            return new Subtask(id, name, desc, status, epicId, startTime, duration);
        } else if (type == TaskType.EPIC) {
            return new Epic(id, name, desc, startTime, duration, endTime);
        } else {
            return new Task(id, name, desc, status, startTime, duration);
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
