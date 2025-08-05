package api;

import model.Subtask;
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
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static util.Status.NEW;

public class SubtasksHandlerTest extends BaseApiTest {

    @Test
    public void getNotEmptySubtasksListIs200Test() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "subtasks"))
                .GET()
                .build();

        taskServer.createBasicTasks();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_OK, response.statusCode(), "Ожидался статус код 200");
        Assertions.assertFalse(response.body().isEmpty());
    }

    @Test
    public void getNotExistSubtaskByIdIs404Test() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "subtasks/404"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_NOT_FOUND, response.statusCode(), "Ожидался статус код 404");
        Assertions.assertEquals("Задачи с таким ID не существует", response.body());
    }

    @Test
    public void getExistSubtaskByIdIs200Test() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "subtasks/3"))
                .GET()
                .build();

        taskServer.createBasicTasks();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_OK, response.statusCode(), "Ожидался статус код 200");
        Assertions.assertEquals(gson.toJson(manager.getTaskById(3)), response.body(), "Задачи не совпадают");
    }

    @Test
    public void createSubtaskIs201Test() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Think", "For first epic", NEW, 2,
                LocalDateTime.now().plusYears(1), Duration.of(90, ChronoUnit.MINUTES));
        String jsonTask = gson.toJson(subtask);

        taskServer.createBasicTasks();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_CREATED, response.statusCode(), "Ожидался статус код 201");
        Assertions.assertEquals(gson.toJson(manager.getTaskById(4)), response.body(), "Задачи не совпадают");
    }

    @Test
    public void updateSubtaskIs201Test() throws IOException, InterruptedException {
        Subtask subtask = new Subtask(3, "Updated subtask", "For first epic", NEW, 2,
                LocalDateTime.now().plusYears(1), Duration.of(90, ChronoUnit.MINUTES));
        String jsonTask = gson.toJson(subtask);

        taskServer.createBasicTasks();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_CREATED, response.statusCode(), "Ожидался статус код 201");
        Assertions.assertEquals("Updated subtask", manager.getTaskById(subtask.getId()).getName(),
                "Задача не обновилась");
    }

    @Test
    public void deleteSubtaskByIdIs200Test() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "subtasks/3"))
                .DELETE()
                .build();

        taskServer.createBasicTasks();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_OK, response.statusCode(), "Ожидался статус код 200");
        Assertions.assertFalse(manager.getTasks().containsKey(3), "Задача не была удалена");
    }

    @Test
    public void deleteSubtaskNotExistIdIs404Test() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "subtasks/404"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_NOT_FOUND, response.statusCode(), "Ожидался статус код 404");
    }
}
