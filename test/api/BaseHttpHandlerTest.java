package api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.HttpURLConnection.*;

public class BaseHttpHandlerTest extends BaseApiTest {

    @Test
    public void wrongEndpointIs404Test() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "wrong/endpoint"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_NOT_FOUND, response.statusCode(), "Ожидался статус код 404");
    }

    @Test
    public void invalidJsonIs400Test() throws IOException, InterruptedException {
        String invalidJson = "{invalid";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(invalidJson))
                .build();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_BAD_REQUEST, response.statusCode(), "Ожидался статус код 400");
    }

    @Test
    public void jsonWithoutRequiredFieldsIs400Test() throws IOException, InterruptedException {
        String taskWithoutFields = """
                {	
                		"description": "Reaaallyy important task",
                		"status": "NEW",
                		"duration": 4320
                }	
                """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskWithoutFields))
                .build();

        HttpResponse<String> response = client.send(request, BASE_HANDLER);

        Assertions.assertEquals(HTTP_BAD_REQUEST, response.statusCode(), "Ожидался статус код 400");
    }

}