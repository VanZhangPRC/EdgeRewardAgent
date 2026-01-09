package van.project.rewardAgent.services;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Keywords {
    String createDate;
    List<List<String>> localNews;
    List<List<String>> nationalNews;
    List<List<String>> funFacts;
    List<List<String>> knowledge;
}
