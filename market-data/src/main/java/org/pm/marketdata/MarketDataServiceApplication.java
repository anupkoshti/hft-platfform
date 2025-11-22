package org.pm.marketdata;


import org.pm.marketdata.service.MarketDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarketDataServiceApplication implements CommandLineRunner {

    private final MarketDataService marketDataService;

    public MarketDataServiceApplication(MarketDataService marketDataService) {
        this.marketDataService = marketDataService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MarketDataServiceApplication.class, args);
    }

    //“When the application starts, run the code inside run() automatically.”

    @Override
    public void run(String... args) {
        System.out.println("Starting Market Data Service...");
//        Called automatically when the application starts
//        Starts your WebSocket connection
        marketDataService.startStreaming();
    }
}
