package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exception.TaskIntersectionException;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.net.HttpURLConnection.*;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected static final String WRONG_ENDPOINT = "Введен неверный адрес (эндпоинт)";
    protected TaskManager manager;
    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
    }

    private void sendText(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] respByte = response.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, respByte.length);
        exchange.getResponseBody().write(respByte);
        exchange.close();
    }

    protected void sendSuccess(HttpExchange exchange, String response) throws IOException {
        sendText(exchange, response, HTTP_OK);
    }

    protected void sendCreated(HttpExchange exchange, String response) throws IOException {
        sendText(exchange, response, HTTP_CREATED);
    }

    protected void sendBadRequest(HttpExchange exchange, String response) throws IOException {
        sendText(exchange, response, HTTP_BAD_REQUEST);
    }

    protected void sendNotFound(HttpExchange exchange, String response) throws IOException {
        sendText(exchange, response, HTTP_NOT_FOUND);
    }

    protected void sendHasIntersections(HttpExchange exchange, String response) throws IOException {
        sendText(exchange, response, HTTP_NOT_ACCEPTABLE);
    }

    protected void sendServerError(HttpExchange exchange, String response) throws IOException {
        sendText(exchange, response, HTTP_INTERNAL_ERROR);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        Endpoint endpoint = getEndpoint(pathParts, exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS -> handleGetTasks(exchange);
            case GET_TASK_BY_ID -> handleGetTaskById(exchange);
            case POST_TASK -> handlePostTask(exchange);
            case DELETE_TASK_BY_ID -> handleDeleteTask(exchange);
            default -> sendNotFound(exchange, WRONG_ENDPOINT);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        switch (pathParts[1]) {
            case "epics" -> {
                List<Task> epics = manager.getTasks().values().stream()
                        .filter(task -> task instanceof Epic)
                        .toList();
                String response = gson.toJson(epics);
                sendSuccess(exchange, response);
            } case "subtasks" -> {
                List<Task> subtasks = manager.getTasks().values().stream()
                        .filter(task -> task instanceof Subtask)
                        .toList();
                sendSuccess(exchange, gson.toJson(subtasks));
            } case "tasks" -> {
                List<Task> tasks = manager.getTasks().values().stream()
                        .filter(task -> !(task instanceof Epic) && !(task instanceof Subtask))
                        .toList();
                sendSuccess(exchange, gson.toJson(tasks));
            } default -> sendNotFound(exchange, WRONG_ENDPOINT);

        }
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String taskType = pathParts[1];
        Optional<Integer> optTaskId = getTaskId(pathParts[2]);
        if (optTaskId.isEmpty()) {
            sendBadRequest(exchange, "Некорректный ID задачи, нужно указать целое число");
        }
        int taskId = optTaskId.get();
        if (!manager.getTasks().containsKey(taskId)) {
            sendNotFound(exchange, "Задачи с таким ID не существует");
        } else {
            if (!isTaskTypeForIdCorrect(taskType, taskId)) {
                sendNotFound(exchange, "ID и тип задачи из запроса не совпадают");
            } else {
                sendSuccess(exchange, gson.toJson(manager.getTaskById(taskId)));
            }
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        String jsonTask = parseJsonFromRequest(exchange);
        Task task = parseTaskFromJson(exchange, jsonTask);

        if (task instanceof Subtask subtask) {
            if (subtask.getName() == null || subtask.getDescription() == null || subtask.getEpicId() == 0) {
                sendBadRequest(exchange, "Поля name, description, subtaskId - обязательны для подзадачи");
            }
        } else if (task.getName() == null || task.getDescription() == null) {
            sendBadRequest(exchange, "Поля name, description - обязательны для любой задачи");
        }

        if (task.getId() == 0) {
            try {
                manager.createTask(task);
                sendCreated(exchange, gson.toJson(task));
            } catch (TaskIntersectionException e) {
                sendHasIntersections(exchange, e.getMessage());
            } catch (Exception e) {
                sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
            }

        } else {
            try {
                manager.updateTask(task);
                sendCreated(exchange, gson.toJson(task));
            } catch (TaskIntersectionException e) {
                sendHasIntersections(exchange, e.getMessage());
            } catch (Exception e) {
                sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
            }
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String taskType = pathParts[1];
        Optional<Integer> optTaskId = getTaskId(pathParts[2]);
        if (optTaskId.isEmpty()) {
            sendBadRequest(exchange, "Некорректный ID задачи, нужно указать целое число");
            return;
        }
        int taskId = optTaskId.get();
        if (!manager.getTasks().containsKey(taskId)) {
            sendNotFound(exchange, "Задачи с таким ID не существует");
        } else {
            if (!isTaskTypeForIdCorrect(taskType, taskId)) {
                sendNotFound(exchange, "ID и тип задачи из запроса не совпадают");
            } else {
                manager.deleteTaskById(taskId);
                sendSuccess(exchange, "Задача с ID: %s - успешно удалена".formatted(taskId));
            }
        }

    }

    private Endpoint getEndpoint(String[] pathParts, String requestMethod) {
        if (pathParts.length == 2 && (pathParts[1].equals("epics") ||
                pathParts[1].equals("subtasks") ||
                pathParts[1].equals("tasks"))) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASKS;
            } else if (requestMethod.equals("POST")) {
                return Endpoint.POST_TASK;
            }
        }

        if (pathParts.length == 3 && pathParts[1].equals("epics") ||
                pathParts[1].equals("subtasks") ||
                pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                return Endpoint.GET_TASK_BY_ID;
            } else if (requestMethod.equals("DELETE")) {
                return Endpoint.DELETE_TASK_BY_ID;
            }
        }

        return Endpoint.UNKNOWN;
    }

    private Optional<Integer> getTaskId(String id) {
        try {
            return Optional.of(Integer.parseInt(id));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private String parseJsonFromRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
    }

    private boolean isTaskTypeForIdCorrect(String taskType, int id) {
        Task task = manager.getTasks().get(id);

        switch (taskType) {
            case "epics" -> {
                return task instanceof Epic;
            } case "subtasks" -> {
                return task instanceof Subtask;
            } case "tasks" -> {
                if (task instanceof Epic || task instanceof Subtask) return false;
            }
        }
        return true;
    }

    private Task parseTaskFromJson(HttpExchange exchange, String jsonTask) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return switch (pathParts[1]) {
                case "epics" -> gson.fromJson(jsonTask, Epic.class);
                case "subtasks" -> gson.fromJson(jsonTask, Subtask.class);
                case "tasks" ->  gson.fromJson(jsonTask, Task.class);
                default -> {
                    sendNotFound(exchange, WRONG_ENDPOINT);
                    yield null;
                }
            };
        } catch (JsonParseException e) {
            sendBadRequest(exchange, "Некорректный JSON: " + e.getMessage());
            return null;
        } catch (Exception e) {
            sendServerError(exchange, "Ошибка сервера: " + e.getMessage());
            return null;
        }
    }
}
