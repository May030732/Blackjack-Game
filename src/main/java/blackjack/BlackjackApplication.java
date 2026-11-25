package blackjack;

import blackjack.repository.HistoryRepository;
import blackjack.repository.MemberRepository;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class BlackjackApplication {

    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--console")) {
            runConsole(args);
            return;
        }

        SpringApplication.run(BlackjackApplication.class, args);
    }

    static void runConsole(String[] args) {
        try (var context = new SpringApplicationBuilder(BlackjackApplication.class)
                .web(WebApplicationType.NONE)
                .run(args)) {
            MemberRepository members = context.getBean(MemberRepository.class);
            HistoryRepository history = context.getBean(HistoryRepository.class);

            new ConsoleGame(members, history).run();
        }
    }
}