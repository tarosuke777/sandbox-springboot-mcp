package com.example.sandbox_springboot_mcp;

import java.net.http.HttpClient;
import java.nio.file.Paths;
import java.util.List;
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
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;

public class McpClinet {
        interface Assistant {
                String chat(String userMessage);
        }

        public static void main(String[] args) throws Exception {

                // String sseUrl = "http://localhost:8080/hms/sse";
                // String sseUrl = "http://localhost:8080/sse";

                // String sseUrl = "http://192.168.10.10/hms/sse";
                // McpTransport transport = new HttpMcpTransport.Builder().sseUrl(sseUrl).build();
                // McpClient client = new DefaultMcpClient.Builder().transport(transport).build();
                // ToolProvider provider = McpToolProvider.builder().mcpClients(client).build();

                // String modelName = "ai/qwen3:latest";
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

                // Embedding専用モデルを使用
                // EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
                // EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                // .baseUrl("http://localhost:12434/engines/llama.cpp/v1")
                // .modelName("bge-large-en-v1.5") // Embedding専用モデル
                // .httpClientBuilder(JdkHttpClient.builder()
                // .httpClientBuilder(HttpClient.newBuilder().version(
                // HttpClient.Version.HTTP_1_1)))
                // .build();


                // PDFの場合、PdfBoxDocumentLoader
                // List<Document> documents = FileSystemDocumentLoader
                // .loadDocuments(Paths.get("knowledge_base.txt")); // 適切なDocumentLoaderを使用

                Document document = FileSystemDocumentLoader
                                .loadDocument(Paths.get("knowledge_base.txt"));
                InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
                EmbeddingStoreIngestor.ingest(document, embeddingStore);

                // EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                // .documentSplitter(splitter).embeddingModel(embeddingModel)
                // .embeddingStore(embeddingStore).build();

                // ingestor.ingest(document);

                // ChatMemory chatMemory =
                // MessageWindowChatMemory.builder().maxMessages(10).build();

                // Assistant assistant = AiServices.builder(Assistant.class).chatModel(model)
                // .toolProvider(provider).chatMemory(chatMemory).build();

                Assistant assistant = AiServices.builder(Assistant.class).chatModel(model)
                                .contentRetriever(EmbeddingStoreContentRetriever.builder()
                                                .embeddingStore(embeddingStore)
                                                // .embeddingModel(embeddingModel) // 任意の埋め込みモデル
                                                .maxResults(3).build())
                                .build();

                // String res = assistant.chat("""
                // /no_think
                // 東京の天気は？いま何時？""");

                // String res = assistant.chat("""
                // /no_think
                // 好きな曲名一覧を取得して""");

                // String res1 = assistant.chat("私の名前は田中太郎で、趣味は釣りです。");
                // System.out.println("AI 応答 1: " + res1);

                // // 2. 記憶された情報を確認する
                // String res2 = assistant.chat("私の趣味は何でしたか？");
                // System.out.println("AI 応答 2: " + res2); // AIは「釣り」と応答するはず

                String res = assistant.chat("""
                                /no_think
                                大阪の2025年10月30日時点の天気は？""");
                System.out.println(res);
                // client.close();

        }


}
