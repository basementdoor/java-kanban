package api;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (exchange.getRequestMethod().equals("GET") && pathParts.length == 2) {
            String response = gson.toJson(manager.getHistory());
            sendSuccess(exchange, response);
        } else {
            sendNotFound(exchange, WRONG_ENDPOINT);
        }
    }
}
