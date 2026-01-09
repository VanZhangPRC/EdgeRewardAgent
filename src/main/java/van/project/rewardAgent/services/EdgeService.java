package van.project.rewardAgent.services;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class EdgeService {

    @Autowired
    private EdgeDriverAgent agent;
    @Autowired
    private KeywordsAgent keywordsAgent;


    @PostConstruct
    public void start() throws IOException {

        Keywords keywords = keywordsAgent.getKeywords();
        log.info("get keywords: {}", keywords);
        agent.searchWeb(keywords);
        agent.searchMobile(keywords);
    }

}
