package org.pm.portfolio.controller;

import lombok.RequiredArgsConstructor;
import org.pm.portfolio.dto.PositionDTO;
import org.pm.portfolio.dto.TradeDTO;
import org.pm.portfolio.model.PortfolioUpdate;
import org.pm.portfolio.producer.PortfolioUpdateProducer;
import org.pm.portfolio.service.PortfolioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final PortfolioUpdateProducer portfolioUpdateProducer;

    @GetMapping("/snapshot")
    public ResponseEntity<PortfolioUpdate> getPortfolioSnapshot() {
        PortfolioUpdate snapshot = portfolioService.getPortfolioSnapshot();
        return ResponseEntity.ok(snapshot);
    }

    @GetMapping("/positions")
    public ResponseEntity<List<PositionDTO>> getAllPositions() {
        return ResponseEntity.ok(portfolioService.getAllPositions());
    }

    @GetMapping("/trades")
    public ResponseEntity<Page<TradeDTO>> getAllTrades(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        return ResponseEntity.ok(portfolioService.getAllTrades(pageable));
    }

    @GetMapping("/trades/symbol/{symbol}")
    public ResponseEntity<Page<TradeDTO>> getTradesBySymbol(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return ResponseEntity.ok(portfolioService.getTradesBySymbol(symbol, pageable));
    }

    @PostMapping("/publish-update")
    public ResponseEntity<String> publishPortfolioUpdate() {
        PortfolioUpdate snapshot = portfolioService.getPortfolioSnapshot();
        portfolioUpdateProducer.publishPortfolioUpdate(snapshot);
        return ResponseEntity.ok("Portfolio update published to Kafka");
    }
}
