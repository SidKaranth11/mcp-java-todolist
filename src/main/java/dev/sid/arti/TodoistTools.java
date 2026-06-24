package dev.sid.arti;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class TodoistTools {

    @Tool(
            name = "list_todoist_tasks",
            description = "List active Todoist tasks. Use this before update or delete when the user gives a task name instead of a task ID."
    )
    public String listTasks() {
        try {
            String response = TodoistClient.listTasks();
            return "Active Todoist tasks retrieved successfully. Response: " + response;
        } catch (Exception e) {
            return error("list Todoist tasks", e);
        }
    }

    @Tool(name = "create_todoist_task", description = "Create a Todoist task")
    public String createTask(
            @ToolParam(description = "Task content or title") String content,
            @ToolParam(description = "Optional task description", required = false) String description,
            @ToolParam(description = "Optional due date like today, tomorrow, next Monday", required = false) String dueString
    ) {
        try {
            String response = TodoistClient.createTask(content, description, dueString);
            return "Todoist task created successfully. Response: " + response;
        } catch (Exception e) {
            return error("create Todoist task", e);
        }
    }

    @Tool(
            name = "update_todoist_task",
            description = "Update a Todoist task by task ID. If the user does not provide a task ID, first call list_todoist_tasks."
    )
    public String updateTask(
            @ToolParam(description = "Todoist task ID") String taskId,
            @ToolParam(description = "New task content or title", required = false) String content,
            @ToolParam(description = "New task description", required = false) String description,
            @ToolParam(description = "New due date", required = false) String dueString
    ) {
        try {
            String response = TodoistClient.updateTask(taskId, content, description, dueString);
            return "Todoist task updated successfully. Response: " + response;
        } catch (Exception e) {
            return error("update Todoist task", e);
        }
    }

    @Tool(
            name = "delete_todoist_task",
            description = "Delete a Todoist task by task ID. If the user does not provide a task ID, first call list_todoist_tasks."
    )
    public String deleteTask(
            @ToolParam(description = "Todoist task ID") String taskId
    ) {
        try {
            return TodoistClient.deleteTask(taskId);
        } catch (Exception e) {
            return error("delete Todoist task", e);
        }
    }

    private String error(String action, Exception e) {
        String message = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
        return "Failed to " + action + ": " + message;
    }
}
