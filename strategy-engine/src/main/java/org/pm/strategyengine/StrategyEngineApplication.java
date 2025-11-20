package org.pm.strategyengine;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class StrategyEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(StrategyEngineApplication.class, args);
    }

    @Bean
    public CommandLineRunner startup() {
        return args -> {
            System.out.println("===========================================");
            System.out.println("Strategy Engine Service Started");
            System.out.println("Listening to: market-ticks");
            System.out.println("Publishing to: trade-signals");
            System.out.println("===========================================");
        };
    }
}
