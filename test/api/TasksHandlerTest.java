package api;

import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static java.net.HttpURLConnection.*;
import static util.Status.NEW;

public class TasksHandlerTest extends BaseApiTest {

    @Test
    public void getNotEmptyTasksListIs200Test() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "tasks"))
                .GET()
                .build();

        taskServer.createBasicTasks();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_OK, response.statusCode(), "Ожидался статус код 200");
        Assertions.assertFalse(response.body().isEmpty());
    }

    @Test
    public void getNotExistTaskByIdIs404Test() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "tasks/404"))
                .GET()
                .build();

        taskServer.createBasicTasks();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_NOT_FOUND, response.statusCode(), "Ожидался статус код 404");
        Assertions.assertEquals("Задачи с таким ID не существует", response.body());
    }

    @Test
    public void getExistTaskByIdIs200Test() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "tasks/1"))
                .GET()
                .build();

        Task testTask = new Task("First task", "Reaaallyy important task", NEW, LocalDateTime.now(),
                Duration.of(3, ChronoUnit.DAYS));
        manager.createTask(testTask);

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_OK, response.statusCode(), "Ожидался статус код 200");
        Assertions.assertEquals(gson.toJson(manager.getTaskById(1)), response.body(), "Задачи не совпадают");
    }

    @Test
    public void createTaskIs201Test() throws IOException, InterruptedException {
        Task testTask = new Task("First task", "Reaaallyy important task", NEW, LocalDateTime.now(),
                Duration.of(3, ChronoUnit.DAYS));
        String jsonTask = gson.toJson(testTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_CREATED, response.statusCode(), "Ожидался статус код 201");
    }

    @Test
    public void createTaskIntersectionErrorTest() throws IOException, InterruptedException {
        Task testTask = new Task("First task", "Reaaallyy important task", NEW, LocalDateTime.now(),
                Duration.of(3, ChronoUnit.DAYS));
        String jsonTask = gson.toJson(testTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse<String> responseOne = client.send(request, BASE_HANDLER);
        Assertions.assertEquals(HTTP_CREATED, responseOne.statusCode(), "Задача не была создана");

        HttpResponse<String> responseTwo = client.send(request, BASE_HANDLER);
        Assertions.assertEquals(HTTP_NOT_ACCEPTABLE, responseTwo.statusCode(), "Ожидался код 406");
        Assertions.assertTrue(manager.getTasks().size() == 1, "Должна быть создана лишь одна задача");
    }

    @Test
    public void updateTaskIs201Test() throws IOException, InterruptedException {
        Task testTask = new Task(1, "Updated Task", "I was updated", NEW, LocalDateTime.now(),
                Duration.of(3, ChronoUnit.DAYS));
        String jsonTask = gson.toJson(testTask);

        taskServer.createBasicTasks();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_CREATED, response.statusCode(), "Ожидался статус код 201");
        Assertions.assertEquals(jsonTask, response.body(), "Задачи не совпадают");
    }

    @Test
    public void deleteTaskByIdIs200Test() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "tasks/1"))
                .DELETE()
                .build();

        Task testTask = new Task("First task", "Reaaallyy important task", NEW, LocalDateTime.now(),
                Duration.of(3, ChronoUnit.DAYS));
        manager.createTask(testTask);

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_OK, response.statusCode(), "Ожидался статус код 200");
        Assertions.assertFalse(manager.getTasks().containsKey(1), "Задача не была удалена");
    }

    @Test
    public void deleteTaskNotExistIdIs404Test() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "tasks/404"))
                .DELETE()
                .build();

        taskServer.createBasicTasks();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_NOT_FOUND, response.statusCode(), "Ожидался статус код 404");
    }

}
