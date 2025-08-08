package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import util.DurationAdapter;
import util.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseApiTest {
    protected final static String BASE_URL = "http://localhost:8080/";
    protected final static HttpResponse.BodyHandler<String> BASE_HANDLER = HttpResponse.BodyHandlers.ofString();
    protected HttpTaskServer taskServer;
    protected TaskManager manager;
    protected HttpClient client;
    protected Gson gson;

    @BeforeEach
    protected void setUp() throws IOException {
        manager = Managers.getInMemory();
        taskServer = new HttpTaskServer(manager);
        client = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        taskServer.start();
    }

    @AfterEach
    protected void tearDown() {
        taskServer.stop();
    }
}
