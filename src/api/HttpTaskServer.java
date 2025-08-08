package api;

import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static util.Status.NEW;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/history", new HistoryHandler(manager));
        server.createContext("/prioritized", new PrioritizedHandler(manager));
        server.createContext("/tasks", new TasksHandler(manager));
        server.createContext("/subtasks", new SubtasksHandler(manager));
        server.createContext("/epics", new EpicsHandler(manager));
    }

    public void start() {
        System.out.println("Сервер запущен на порте: " + PORT);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен.");
    }

    public void createBasicTasks() {
        Task task = new Task("First task", "Reaaallyy important task", NEW, LocalDateTime.now(),
                Duration.of(3, ChronoUnit.DAYS));
        manager.createTask(task);
        manager.getTaskById(task.getId());

        Epic epic = new Epic("First epic", "Contains one task");
        manager.createTask(epic);
        manager.getTaskById(epic.getId());

        Subtask subtask = new Subtask("Think", "For first epic", NEW, epic.getId(),
                LocalDateTime.of(2025, 7, 15, 18, 30), Duration.of(90, ChronoUnit.MINUTES));
        manager.createTask(subtask);
        manager.getTaskById(subtask.getId());
    }

    public static void main(String[] args) throws IOException {

        HttpTaskServer taskServer = new HttpTaskServer(Managers.getInMemory());
        taskServer.start();
        taskServer.createBasicTasks();
    }
}
