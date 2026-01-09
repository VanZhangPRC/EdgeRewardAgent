package van.project.rewardAgent.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EdgeService {

    @Autowired
    private EdgeDriverAgent agent;

    @Value("classpath:/newsKeywords.json")
    Resource newsKeywords;
    @Autowired
    private ObjectMapper objectMapper;

    private Keywords getKeywords() throws IOException {
        // TODO. 修改为自动获取热搜词
        String keywordData = new BufferedReader(new InputStreamReader(newsKeywords.getInputStream()))
                .lines()
                .collect(Collectors.joining());
        return objectMapper.readValue(keywordData, new TypeReference<Keywords>() {
        });
    }



    @PostConstruct
    public void start() throws IOException {

        Keywords keywords = getKeywords();
        log.info("get keywords: {}", keywords);
        agent.searchWeb(keywords);
        agent.searchMobile(keywords);
    }

}
