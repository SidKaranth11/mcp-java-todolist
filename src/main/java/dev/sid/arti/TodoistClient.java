package dev.sid.arti;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TodoistClient {

    private static final String BASE_URL = "https://api.todoist.com/api/v1";
    private static final String TOKEN = System.getenv("TODOLIST_API_TOKEN");

    // One HttpClient shared by everything
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    // The single place where HTTP happens
    private static String request(String method, String path, String body) throws Exception {
        if (TOKEN == null || TOKEN.isEmpty()) {
            throw new RuntimeException("MISSING API TOKEN: Set TODOLIST_API_TOKEN in environment variables");
        }

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Authorization", "Bearer " + TOKEN)
                .header("Content-Type", "application/json")
                .method(method, body == null
                        ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());

        if (res.statusCode() >= 400) {
            throw new RuntimeException("Request failed (status " + res.statusCode() + "): " + res.body());
        }

        return res.body();
    }

    public static String createTask(String content, String description, String dueString) throws Exception {
        StringBuilder json = new StringBuilder("{\"content\":\"" + escapeJson(content) + "\"");

        if (description != null && !description.isEmpty()) {
            json.append(",\"description\":\"").append(escapeJson(description)).append("\"");
        }

        if (dueString != null && !dueString.isEmpty()) {
            json.append(",\"due_string\":\"").append(escapeJson(dueString)).append("\"");
        }

        json.append("}");

        return request("POST", "/tasks", json.toString());
    }

    public static String deleteTask(String taskId) throws Exception {
        request("DELETE", "/tasks/" + taskId, null);
        return "Deleted task " + taskId;
    }

    public static String updateTask(String taskId, String content, String description, String dueString) throws Exception {

        StringBuilder json = new StringBuilder();
        json.append("{");

        boolean hasField = false;

        if (content != null && !content.isEmpty()) {
            json.append("\"content\":\"").append(escapeJson(content)).append("\"");
            hasField = true;
        }

        if (description != null && !description.isEmpty()) {
            if (hasField) json.append(",");
            json.append("\"description\":\"").append(escapeJson(description)).append("\"");
            hasField = true;
        }

        if (dueString != null && !dueString.isEmpty()) {
            if (hasField) json.append(",");
            json.append("\"due_string\":\"").append(escapeJson(dueString)).append("\"");
            hasField = true;
        }

        json.append("}");

        if (!hasField) {
            throw new RuntimeException("At least one field is required to update task");
        }

        return request(
                "POST",
                "/tasks/" + taskId,
                json.toString()
        );
    }

    private static String escapeJson(String value) {
        if (value == null) return "";

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Usage:");
            System.out.println("java TodoistClient \"Task title\" \"Description\" \"Due date\"");
            return;
        }

        String content = args[0];
        String description = args.length > 1 ? args[1] : null;
        String dueString = args.length > 2 ? args[2] : null;

        String response = TodoistClient.createTask(
                content,
                description,
                dueString
        );

        System.out.println(response);
    }
}
