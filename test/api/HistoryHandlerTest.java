package api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

public class HistoryHandlerTest extends BaseApiTest {

    @Test
    public void emptyHistoryIs404Test() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_NOT_FOUND, response.statusCode(), "Ожидался статус код 404");
    }

    @Test
    public void getNotEmptyHistoryIs200Test() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "history"))
                .GET()
                .build();

        taskServer.createBasicTasks();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_OK, response.statusCode(), "Ожидался статус код 200");
        Assertions.assertEquals(gson.toJson(manager.getHistory()), response.body(),
                "Ответ отличается от истории в менеджере");
    }
}
