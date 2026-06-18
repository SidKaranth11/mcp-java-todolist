package dev.sid.arti;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class TodoistTools {

    @Tool(name = "create_todoist_task", description = "Create a Todoist task")
    public String createTask(
            @ToolParam(description = "Task content or title") String content,
            @ToolParam(description = "Optional task description", required = false) String description,
            @ToolParam(description = "Optional due date like today, tomorrow, next Monday", required = false) String dueString
    ) throws Exception {
        return TodoistClient.createTask(content, description, dueString);
    }

    @Tool(name = "delete_todoist_task", description = "Delete a Todoist task by task ID")
    public String deleteTask(
            @ToolParam(description = "Todoist task ID") String taskId
    ) throws Exception {
        return TodoistClient.deleteTask(taskId);
    }

    @Tool(name = "update_todoist_task", description = "Update a Todoist task")
    public String updateTask(
            @ToolParam(description = "Todoist task ID") String taskId,
            @ToolParam(description = "New task content or title", required = false) String content,
            @ToolParam(description = "New task description", required = false) String description,
            @ToolParam(description = "New due date", required = false) String dueString
    ) throws Exception {
        return TodoistClient.updateTask(taskId, content, description, dueString);
    }
}
