package com.example.sandbox_springboot_mcp;

import java.net.http.HttpClient;
import java.nio.file.Paths;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.http.client.jdk.JdkHttpClient;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class McpClinet {
        interface Assistant {
                String chat(String userMessage);
        }

        public static void main(String[] args) throws Exception {

                // String sseUrl = "http://localhost:8080/hms/sse";
                // String sseUrl = "http://localhost:8080/sse";

                String sseUrl = "http://192.168.10.10/hms/sse";
                McpTransport transport = new HttpMcpTransport.Builder().sseUrl(sseUrl).build();
                McpClient client = new DefaultMcpClient.Builder().transport(transport).build();
                ToolProvider provider = McpToolProvider.builder().mcpClients(client).build();

                // String modelName = "qwen/qwen3-1.7b";
                String modelName = "ai/qwen3:0.6B-F16";
                ChatModel model = OpenAiChatModel.builder()
                                // .baseUrl("http://localhost:1234/v1")
                                .baseUrl("http://localhost:12434/engines/llama.cpp/v1")
                                .modelName(modelName)
                                .httpClientBuilder(JdkHttpClient.builder()
                                                .httpClientBuilder(HttpClient.newBuilder().version(
                                                                HttpClient.Version.HTTP_1_1)))
                                .build();

                EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
                // EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                // .baseUrl("http://localhost:12434/engines/llama.cpp/v1")
                // .modelName(modelName).build();

                EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
                DocumentSplitter splitter = DocumentSplitters.recursive(1000, 0);

                EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                                .documentSplitter(splitter).embeddingModel(embeddingModel)
                                .embeddingStore(embeddingStore).build();

                // PDFの場合、PdfBoxDocumentLoader
                Document document = FileSystemDocumentLoader
                                .loadDocument(Paths.get("knowledge_base.txt")); // 適切なDocumentLoaderを使用

                ingestor.ingest(document);

                Assistant assistant = AiServices.builder(Assistant.class).chatModel(model)
                                .toolProvider(provider).build();
                // String res = assistant.chat("""
                // /no_think
                // 東京の天気は？いま何時？""");

                // String res = assistant.chat("""
                // /no_think
                // 好きな曲名一覧を取得して""");

                String res = assistant.chat("""
                                /no_think
                                東京の2025年10月30日時点の天気は？""");


                System.out.println(res);
                client.close();

        }


}
