package api;

import model.Epic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.HttpURLConnection.*;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public class EpicsHandlerTest extends BaseApiTest {

    @Test
    public void getNotEmptyEpicsListIs200Test() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "epics"))
                .GET()
                .build();

        taskServer.createBasicTasks();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_OK, response.statusCode(), "Ожидался статус код 200");
        Assertions.assertFalse(response.body().isEmpty());
    }

    @Test
    public void getNotExistEpicByIdIs404Test() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "epics/404"))
                .GET()
                .build();

        taskServer.createBasicTasks();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_NOT_FOUND, response.statusCode(), "Ожидался статус код 404");
        Assertions.assertEquals("Задачи с таким ID не существует", response.body());
    }

    @Test
    public void getExistEpicByIdIs200Test() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "epics/2"))
                .GET()
                .build();

        taskServer.createBasicTasks();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_OK, response.statusCode(), "Ожидался статус код 200");
        Assertions.assertEquals(gson.toJson(manager.getTaskById(2)), response.body(), "Задачи не совпадают");
    }

    @Test
    public void createEpicIs201Test() throws IOException, InterruptedException {
        Epic epicTask = new Epic("Epic", "Reaaallyy important epic");
        String jsonTask = gson.toJson(epicTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_CREATED, response.statusCode(), "Ожидался статус код 201");
        Assertions.assertEquals(gson.toJson(manager.getTaskById(1)), response.body(), "Задачи не совпадают");
    }

    @Test
    public void updateEpicIs201Test() throws IOException, InterruptedException {
        Epic epicTask = new Epic(2,"Updated Epic", "I was updated");
        String jsonTask = gson.toJson(epicTask);

        taskServer.createBasicTasks();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_CREATED, response.statusCode(), "Ожидался статус код 201");
        Assertions.assertEquals("Updated Epic", manager.getTaskById(2).getName(), "Задача не обновилась");
    }

    @Test
    public void deleteTaskByIdIs200Test() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "epics/2"))
                .DELETE()
                .build();

        taskServer.createBasicTasks();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_OK, response.statusCode(), "Ожидался статус код 200");
        Assertions.assertFalse(manager.getTasks().containsKey(2), "Задача не была удалена");
    }

    @Test
    public void deleteEpicNotExistIdIs404Test() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "epics/404"))
                .DELETE()
                .build();

        taskServer.createBasicTasks();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_NOT_FOUND, response.statusCode(), "Ожидался статус код 404");
    }
}
