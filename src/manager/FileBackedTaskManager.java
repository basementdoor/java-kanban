package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import util.CSVFormatter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
    }

    @Override
    public Task getTaskById(int id) {
        return super.getTaskById(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return super.getSubtaskById(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return super.getEpicById(id);
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return super.getHistory();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSVFormatter.getHeader());
            writer.newLine();

            for (Task task : getTasks().values()) {
                writer.write(CSVFormatter.taskToString(task));
                writer.newLine();
            }

            for (Subtask subtask : getSubtasks().values()) {
                writer.write(CSVFormatter.taskToString(subtask));
                writer.newLine();
            }

            for (Epic epic : getEpics().values()) {
                writer.write(CSVFormatter.taskToString(epic));
                writer.newLine();
            }

            writer.newLine();
            if (!historyManager.getHistory().isEmpty()) {
                writer.write(CSVFormatter.historyToString(historyManager));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerSaveException("Ошибка при сохранении данных менеджера");
        }
    }

    private static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        try {
            var fileData = Files.readString(file.toPath());
            String[] fileDataSplit = fileData.split(System.lineSeparator());
            for (int i = 0; i < fileDataSplit.length; i++) {
                String line = fileDataSplit[i];
                if (line.isBlank()) {
                    if (i + 1 < fileDataSplit.length) {
                        line = fileDataSplit[i + 1];
                        String[] historyId = CSVFormatter.historyFromString(line);
                        for (String stringId : historyId) {
                            int id = Integer.parseInt(stringId);

                            if (taskManager.getTasks().containsKey(id)) {
                                taskManager.historyManager.add(taskManager.getTasks().get(id));
                            } else if (taskManager.getSubtasks().containsKey(id)) {
                                taskManager.historyManager.add(taskManager.getSubtasks().get(id));
                            } else if (taskManager.getEpics().containsKey(id)) {
                                taskManager.historyManager.add(taskManager.getEpics().get(id));
                            }
                        }
                    }
                } else {
                    Task task = CSVFormatter.taskFromString(line);
                    taskManager.addTask(task);

                    if (task.getId() > taskManager.uniqueId) {
                        uniqueId = task.getId() + 1;
                    }
                }
            }
            for (Subtask subtask : taskManager.getSubtasks().values()) {
                int epicId = subtask.getEpicId();
                Epic epic = taskManager.epics.get(epicId);
                epic.addSubtask(subtask.getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerReadException("Не удалось прочитать файл: " + file);
        }


        return taskManager;
    }

    private void addTask(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            subtasks.put(subtask.getId(), subtask);
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            epics.put(epic.getId(), epic);
        } else tasks.put(task.getId(), task);
    }
}
