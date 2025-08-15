package com.example.sandbox_springboot_mcp;

import java.util.Map;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class TrainingService {
    @Tool(description = "日付でトレーニングを取得する", name = "getTraining")
    public String getTraining(@ToolParam(description = "日付") String date) {
        var dateWithTraining =
                Map.of("2025-01-01", "チェストプレス", "2025-01-02", "ランニング", "2025-01-03", "水泳");
        return dateWithTraining.getOrDefault(date, "No training scheduled for this date");
    }

}
