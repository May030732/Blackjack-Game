
package blackjack.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// 告訴 Spring Boot：這個類別負責接收 HTTP 請求
@RestController
public class TestController {

    // 當瀏覽器訪問 /api/test 時，執行這個方法
    @GetMapping("/api/test")
    public String test() {
        return "Blackjack Spring Boot is running!";
    }
}

