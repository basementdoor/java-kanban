package manager;

import exception.ManagerReadException;
import exception.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;
import util.CSVFormatter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        var task = tasks.get(id);
        historyManager.add(task);
        save();
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    public File getFile() {
        return file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSVFormatter.getHeader());
            writer.newLine();

            for (Task task : getTasks().values()) {
                writer.write(CSVFormatter.taskToString(task));
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

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        try {
            var fileData = Files.readString(file.toPath());
            String[] fileDataSplit = fileData.split(System.lineSeparator());
            for (int i = 1; i < fileDataSplit.length - 1; i++) {
                String line = fileDataSplit[i];
                /*
                Далее if сделан, чтобы понимать, когда заканчиваются таски, и начинается менеджер истории.
                Логика: когда встречается пустая строка (ее добавляли при сохранении после всех тасок) - пропускаем
                пустую, и из следующей восстанавливаем историю (поэтому i < fileDataSplit.length - 1 т.к. для последней
                строки логика обработки задана в этом if). Если истории нет - этот if наступать не должен (нет пустых
                строк)
                */
                if (line.isBlank()) {
                    if (i + 1 < fileDataSplit.length) {
                        line = fileDataSplit[i + 1];
                        String[] historyId = CSVFormatter.historyFromString(line);
                        for (String stringId : historyId) {
                            int id = Integer.parseInt(stringId);

                            if (taskManager.getTasks().containsKey(id)) {
                                taskManager.historyManager.add(taskManager.getTasks().get(id));
                            }
                        }
                    }
                } else {
                    Task task = CSVFormatter.taskFromString(line);
                    taskManager.addTask(task);

                    if (task.getId() > taskManager.uniqueId) {
                        taskManager.uniqueId = task.getId() + 1;
                    }
                }
            }
            for (Task task : taskManager.getTasks().values()) {
                if (task instanceof Subtask) {
                    Subtask subtask = (Subtask) task;
                    Epic epic = (Epic) taskManager.tasks.get(subtask.getEpicId());
                    epic.addSubtask(subtask.getId());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new ManagerReadException("Не удалось прочитать файл: " + file);
        }

        return taskManager;
    }

    private void addTask(Task task) {
        tasks.put(task.getId(), task);
    }
}
