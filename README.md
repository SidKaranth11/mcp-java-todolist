# Todoist MCP Server for Render

This is a Spring Boot MCP server that exposes Todoist task actions as MCP tools.

## Tools

- create_todoist_task
- update_todoist_task
- delete_todoist_task

## Render deployment

1. Push this folder to GitHub.
2. In Render, create a new Web Service.
3. Select your GitHub repository.
4. Use Docker deployment. Render will use the included Dockerfile.
5. Add this environment variable in Render:

```text
TODOLIST_API_TOKEN=your_todoist_api_token
```

6. Deploy the service.

## Local Docker test

```bash
docker build -t todoist-mcp .
docker run -p 8080:8080 -e TODOLIST_API_TOKEN=your_todoist_api_token todoist-mcp
```

Default server port is 8080 locally. On Render, it uses the dynamic `PORT` environment variable.
