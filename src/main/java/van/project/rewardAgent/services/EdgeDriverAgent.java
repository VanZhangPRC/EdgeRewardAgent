package van.project.rewardAgent.services;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class EdgeDriverAgent {

    @Value("${van.project.rewardAgent.edge-user-dir}")
    private String userDataDir;
    @Value("${van.project.rewardAgent.edge-user-profile}")
    private String userProfile;

    private EdgeDriver createDriver() throws IOException {
        return createDriver(false);
    }

    private EdgeDriver createDriver(boolean isMobile) throws IOException {
        log.info("starting edge service");
        EdgeOptions options = new EdgeOptions();

//        options.setBinary("B:\\WorkSpace\\van-s-project\\RewardAgent\\src\\main\\resources\\edgedriver_win64\\msedgedriver.exe");

        // 自动登录浏览器
        // 使用已存在的用户数据目录
        options.addArguments("--user-data-dir=" + userDataDir);
        // 指定具体 Profile（很重要）
        options.addArguments("--profile-directory="+ userProfile);

        // 可选：关闭自动化提示
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-blink-features=AutomationControlled");

        if (isMobile) {
            options.setExperimentalOption("mobileEmulation", Collections.singletonMap("deviceName", "iPhone 14 Pro Max"));
        }

        return new EdgeDriver(options);
    }

    private void search(EdgeDriver driver, List<String> keywords) {
        driver.navigate().to("https://cn.bing.com/");

        WebElement element = driver.findElement(By.id("sb_form_q"));
        element.sendKeys(String.join(" ", keywords));
        element.submit();

        // 尝试点击用户界面获取积分信息，手机端第一次搜索似乎需要点击一次才能使积分生效，也可能是错觉
        try {
            Thread.sleep(1000);
            driver.findElement(By.id("id_rh_w")).click();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            driver.findElement(By.id("mHamburger")).click();
        }

        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void searchWeb(Keywords keywords) throws IOException {
        // 启动浏览器
        EdgeDriver driver = createDriver();

        // 开始网页端搜索
        log.info("starting searching in web model");
        log.info("start searching local news with keywords");
        keywords.getLocalNews().forEach(w -> {
            search(driver, w);
        });

        log.info("start searching national news with keywords");
        keywords.getNationalNews().forEach(w -> {
            search(driver, w);
        });

        driver.quit();
    }

    public void searchMobile(Keywords keywords) throws IOException {
        EdgeDriver driver = createDriver(true);

        log.info("starting searching in mobile model");

        log.info("start searching fun facts with keywords");
        keywords.getFunFacts().forEach(w -> {
            search(driver, w);
        });

        log.info("start searching knowledge with keywords");
        keywords.getKnowledge().forEach(w -> {
            search(driver, w);
        });

        driver.quit();
    }
}
