package api;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET") && pathParts.length == 2) {
            var prioritizedTasks = manager.getPrioritizedTasks();

            if (prioritizedTasks.isEmpty()) sendNotFound(exchange, "Список задач пуст");
            else sendSuccess(exchange, gson.toJson(prioritizedTasks));

        } else {
            sendNotFound(exchange, WRONG_ENDPOINT);
        }
    }
}
