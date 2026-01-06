package van.project.rewardAgent.services;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Keywords {
    List<List<String>> localNews;
    List<List<String>> nationalNews;
    List<List<String>> funFacts;
    List<List<String>> knowledge;
}
