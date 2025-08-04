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

    public static void main(String[] args) throws IOException {

        HttpTaskServer taskServer = new HttpTaskServer(Managers.getDefault());
        taskServer.start();

        Task simpleTask = new Task("First task", "Reaaallyy important task", NEW, LocalDateTime.now(),
                Duration.of(3, ChronoUnit.DAYS));
        taskServer.manager.createTask(simpleTask);
        taskServer.manager.getTaskById(simpleTask.getId());
        System.out.println(taskServer.manager.getHistory());

        Epic oneTaskEpic = new Epic("First epic", "Contains one task");
        taskServer.manager.createTask(oneTaskEpic);
        taskServer.manager.getTaskById(oneTaskEpic.getId());
        System.out.println(taskServer.manager.getHistory());

        Subtask subtaskFirstEpic = new Subtask("Think", "For first epic", NEW, oneTaskEpic.getId(),
                LocalDateTime.of(2025, 7, 15, 18, 30), Duration.of(90, ChronoUnit.MINUTES));
        taskServer.manager.createTask(subtaskFirstEpic);
        taskServer.manager.getTaskById(subtaskFirstEpic.getId());
    }
}
