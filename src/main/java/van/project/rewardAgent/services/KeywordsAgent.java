package van.project.rewardAgent.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Slf4j
@Component
public class KeywordsAgent {
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${van.project.rewardAgent.keywords-mode:local}")
    private String keywordsMode;

    // ~ DeepSeek Staff
    // ===================================
    @Value("classpath:/talkToAI.txt")
    private Resource talkToAI;
    @Autowired
    private DeepSeekChatModel chatModel;

    // ~ Local Staff
    // ===================================
    @Value("classpath:/newsKeywords.json")
    private Resource newsKeywords;

    private Keywords getKeywordsFromDeepSeek() {
        Keywords keywords = null;

        File localCache = new File("./keywords-from-ai-cache.json");
        if (localCache.exists()) {
            log.info("存在缓存文件，尝试从缓存中获取关键词进行搜索");
            try {
                keywords = objectMapper.readValue(localCache, new TypeReference<Keywords>() {});
                // 是否3天内缓存，是则继续使用
                boolean isIn3Days = LocalDate.parse(keywords.getCreateDate(), DateTimeFormatter.ISO_LOCAL_DATE).plusDays(4).isAfter(LocalDate.now());
                if (isIn3Days) {
                    log.info("获得3天内缓存，使用缓存关键词进行查询");
                    return keywords;
                }
            } catch (IOException e) {
                log.error("缓存文件不可用，重新获取关键词");
                throw new RuntimeException(e);
            }
        }

        log.info("从DeepSeek获取可搜索的关键词");
        String talkToAIText;
        try {
            talkToAIText = new BufferedReader(new InputStreamReader(talkToAI.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            log.error("读取预设文件错误：talkToAI.txt", e);
            throw new RuntimeException(e);
        }

        // 设置为思考模式
        DeepSeekChatOptions promptOptions = DeepSeekChatOptions.builder()
                .model(DeepSeekApi.ChatModel.DEEPSEEK_REASONER.getValue())
                .build();
        // 构建对话
        Prompt prompt = new Prompt(talkToAIText, promptOptions);
        ChatResponse response = chatModel.call(prompt);
        // 获取内容
        String keywordsStr = response.getResult().getOutput().getText();

        // markdown 格式处理
        if (keywordsStr != null && keywordsStr.startsWith("```json")) {
            keywordsStr = keywordsStr.replace("```json", "");
            keywordsStr = keywordsStr.replace("```", "");
        }

        log.info(keywordsStr);
        try {
            keywords = objectMapper.readValue(keywordsStr, new TypeReference<Keywords>() {});

            log.info("从AI获取的关键词写入缓存文件");
            keywords.setCreateDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(localCache, keywords);
        } catch (JsonProcessingException e) {
            log.error("读取AI返回关键词内容错误", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("写入缓存文件失败，删除文件：keywords-from-ai-cache.json");
            localCache.delete();
        }
        return keywords;
    }

    private Keywords getKeywordsFromLocal() {
        try {
            String keywordData = new BufferedReader(new InputStreamReader(newsKeywords.getInputStream()))
                    .lines()
                    .collect(Collectors.joining());
            return objectMapper.readValue(keywordData, new TypeReference<Keywords>() {});
        } catch (Exception e) {
            log.error("读取预设文件错误：newsKeywords.json", e);
            throw new RuntimeException(e);
        }
    }


    public Keywords getKeywords() {
        return switch (keywordsMode) {
            case "deepseek" -> getKeywordsFromDeepSeek();
            case "local" -> getKeywordsFromLocal();
            default -> getKeywordsFromLocal();
        };
    }
}
