package com.example.sandbox_springboot_mcp;

import java.util.List;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class MusicService {
    @Tool(description = "好きな曲名の一覧を取得する")
    public String getMusicList() {
        var music = List.of("レペゼン", "花鳥風月", "ヴァーミリオン", "サクラメイキュウ", "ウィアートル");
        return String.join(", ", music);
    }
}
