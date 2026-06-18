package dev.sid.arti;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ArtiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtiApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider todoistToolCallbacks(TodoistTools todoistTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(todoistTools)
                .build();
    }
}
