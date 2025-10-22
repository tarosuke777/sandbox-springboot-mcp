package com.example.sandbox_springboot_mcp;

import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.model.chat.ChatModel;
import java.net.http.HttpClient;


public class McpClinet {
        interface Assistant {
                String chat(String userMessage);
        }

        public static void main(String[] args) throws Exception {

                McpTransport transport = new HttpMcpTransport.Builder()
                                .sseUrl("http://localhost:8080/sse").build();
                McpClient client = new DefaultMcpClient.Builder().transport(transport).build();
                ToolProvider provider = McpToolProvider.builder().mcpClients(client).build();

                String modelName = "qwen/qwen3-1.7b";
                ChatModel model = OpenAiChatModel.builder().baseUrl("http://localhost:1234/v1")
                                .modelName(modelName)
                                .httpClientBuilder(JdkHttpClient.builder()
                                                .httpClientBuilder(HttpClient.newBuilder().version(
                                                                HttpClient.Version.HTTP_1_1)))
                                .build();
                Assistant assistant = AiServices.builder(Assistant.class).chatModel(model)
                                .toolProvider(provider).build();
                // String res = assistant.chat("""
                // /no_think
                // 東京の天気は？いま何時？""");

                String res = assistant.chat("""
                                /no_think
                                好きな曲名リストを教えて""");

                System.out.println(res);
                client.close();

        }


}
