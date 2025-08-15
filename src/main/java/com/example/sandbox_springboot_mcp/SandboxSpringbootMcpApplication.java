package com.example.sandbox_springboot_mcp;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SandboxSpringbootMcpApplication {

	public static void main(String[] args) {
		SpringApplication.run(SandboxSpringbootMcpApplication.class, args);
	}

	@Bean
	public ToolCallbackProvider allTools(MusicService musicService,
			TrainingService trainingService) {
		return MethodToolCallbackProvider.builder().toolObjects(musicService, trainingService)
				.build();
	}

}
