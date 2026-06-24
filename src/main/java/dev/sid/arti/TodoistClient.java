package dev.sid.arti;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class TodoistClient {

    private static final String BASE_URL = "https://api.todoist.com/api/v1";

    private static final String TOKEN = firstNonBlank(
            System.getenv("TODOIST_API_TOKEN"),
            System.getenv("TODOLIST_API_TOKEN")
    );

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static String request(String method, String path, String body) throws Exception {
        if (TOKEN == null || TOKEN.isBlank()) {
            throw new RuntimeException(
                    "Missing Todoist API token. Set TODOIST_API_TOKEN in Render environment variables."
            );
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .timeout(Duration.ofSeconds(20))
                .header("Authorization", "Bearer " + TOKEN)
                .header("Accept", "application/json");

        if (body != null) {
            builder.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        HttpResponse<String> response = CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new RuntimeException(
                    "Todoist API request failed. HTTP " + response.statusCode() + ": " + response.body()
            );
        }

        return response.body() == null ? "" : response.body();
    }

    public static String listTasks() throws Exception {
        return request("GET", "/tasks", null);
    }

    public static String createTask(String content, String description, String dueString) throws Exception {
        if (content == null || content.isBlank()) {
            throw new RuntimeException("Task content/title is required.");
        }

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"content\":\"").append(escapeJson(content.trim())).append("\"");

        if (description != null && !description.isBlank()) {
            json.append(",\"description\":\"").append(escapeJson(description.trim())).append("\"");
        }

        if (dueString != null && !dueString.isBlank()) {
            json.append(",\"due_string\":\"").append(escapeJson(dueString.trim())).append("\"");
        }

        json.append("}");

        return request("POST", "/tasks", json.toString());
    }

    public static String updateTask(String taskId, String content, String description, String dueString) throws Exception {
        validateTaskId(taskId);

        StringBuilder json = new StringBuilder();
        json.append("{");

        boolean hasField = false;

        if (content != null && !content.isBlank()) {
            json.append("\"content\":\"").append(escapeJson(content.trim())).append("\"");
            hasField = true;
        }

        if (description != null && !description.isBlank()) {
            if (hasField) json.append(",");
            json.append("\"description\":\"").append(escapeJson(description.trim())).append("\"");
            hasField = true;
        }

        if (dueString != null && !dueString.isBlank()) {
            if (hasField) json.append(",");
            json.append("\"due_string\":\"").append(escapeJson(dueString.trim())).append("\"");
            hasField = true;
        }

        json.append("}");

        if (!hasField) {
            throw new RuntimeException("At least one update field is required: content, description, or dueString.");
        }

        return request("POST", "/tasks/" + urlEncode(taskId.trim()), json.toString());
    }

    public static String deleteTask(String taskId) throws Exception {
        validateTaskId(taskId);
        request("DELETE", "/tasks/" + urlEncode(taskId.trim()), null);
        return "Task deleted successfully. Task ID: " + taskId.trim();
    }

    private static void validateTaskId(String taskId) {
        if (taskId == null || taskId.isBlank()) {
            throw new RuntimeException("Todoist task ID is required. First call list_todoist_tasks to find the task ID.");
        }
    }

    private static String firstNonBlank(String... values) {
        if (values == null) return null;

        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }

        return null;
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
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
}