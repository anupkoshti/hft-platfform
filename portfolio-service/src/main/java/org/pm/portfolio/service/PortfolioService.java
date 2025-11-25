package org.pm.portfolio.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pm.portfolio.dto.PositionDTO;
import org.pm.portfolio.dto.TradeDTO;
import org.pm.portfolio.model.Position;
import org.pm.portfolio.model.PortfolioUpdate;
import org.pm.portfolio.model.Trade;
import org.pm.portfolio.repository.PositionRepository;
import org.pm.portfolio.repository.TradeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioService {

    private final PositionRepository positionRepository;
    private final TradeRepository tradeRepository;

    @Transactional
    @CacheEvict(value = {"positions", "portfolioSnapshot"}, allEntries = true)
    public void processOrderStatus(String tradeId, String brokerOrderId, String symbol, 
                                   String side, double price, String status) {
        log.info("Processing order status: {} {} {} @ {} - {}", side, symbol, tradeId, price, status);

        // Save trade record
        Trade trade = new Trade(tradeId, brokerOrderId, symbol, side, price, 1.0, status, 
                               System.currentTimeMillis());
        tradeRepository.save(trade);

        // Update position only if order is FILLED
        if ("FILLED".equals(status)) {
            updatePosition(symbol, side, price, 1.0);
        }
    }

    @Transactional
    public void updatePosition(String symbol, String side, double price, double quantity) {
        Position position = positionRepository.findBySymbol(symbol)
                .orElse(new Position(symbol));

        if ("BUY".equalsIgnoreCase(side)) {
            double currentQty = position.getQuantity();
            double currentAvgPrice = Double.isNaN(position.getAveragePrice()) ? 0.0 : position.getAveragePrice();
            
            double totalCost = (currentQty * currentAvgPrice) + (price * quantity);
            double newQuantity = currentQty + quantity;
            position.setQuantity(newQuantity);
            position.setAveragePrice(newQuantity > 0 ? totalCost / newQuantity : 0.0);
        } else if ("SELL".equalsIgnoreCase(side)) {
            double avgPrice = Double.isNaN(position.getAveragePrice()) ? 0.0 : position.getAveragePrice();
            double realizedPnL = (price - avgPrice) * quantity;
            double currentRealizedPnL = Double.isNaN(position.getRealizedPnL()) ? 0.0 : position.getRealizedPnL();
            position.setRealizedPnL(currentRealizedPnL + realizedPnL);
            position.setQuantity(position.getQuantity() - quantity);
        }

        position.setCurrentPrice(price);
        position.setLastUpdated(System.currentTimeMillis());
        calculateUnrealizedPnL(position);
        
        positionRepository.save(position);
        log.info("Updated position: {}", position);
    }

    @Transactional
    public void updateMarketPrice(String symbol, double price) {
        // Only update if position exists and price changed significantly (> 0.01% to reduce DB writes)
        positionRepository.findBySymbol(symbol).ifPresent(position -> {
            double priceDiff = Math.abs(position.getCurrentPrice() - price);
            double priceChangePercent = (priceDiff / position.getCurrentPrice()) * 100;
            
            // Only update if price changed by more than 0.01% or it's been more than 5 seconds
            long timeSinceUpdate = System.currentTimeMillis() - position.getLastUpdated();
            if (priceChangePercent > 0.01 || timeSinceUpdate > 5000) {
                position.setCurrentPrice(price);
                position.setLastUpdated(System.currentTimeMillis());
                calculateUnrealizedPnL(position);
                positionRepository.save(position);
                log.debug("Updated market price for {}: {} (change: {}%)", symbol, price, priceChangePercent);
            }
        });
    }

    private void calculateUnrealizedPnL(Position position) {
        if (position.getQuantity() > 0) {
            double avgPrice = Double.isNaN(position.getAveragePrice()) ? 0.0 : position.getAveragePrice();
            double currentPrice = Double.isNaN(position.getCurrentPrice()) ? 0.0 : position.getCurrentPrice();
            position.setUnrealizedPnL((currentPrice - avgPrice) * position.getQuantity());
        } else {
            position.setUnrealizedPnL(0.0);
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "portfolioSnapshot", unless = "#result == null")
    public PortfolioUpdate getPortfolioSnapshot() {
        List<Position> positions = positionRepository.findAll();
        
        double totalRealizedPnL = 0.0;
        double totalUnrealizedPnL = 0.0;
        double totalValue = 0.0;
        
        // Single pass through positions for all calculations
        for (Position p : positions) {
            double realizedPnL = Double.isNaN(p.getRealizedPnL()) ? 0.0 : p.getRealizedPnL();
            double unrealizedPnL = Double.isNaN(p.getUnrealizedPnL()) ? 0.0 : p.getUnrealizedPnL();
            double currentPrice = Double.isNaN(p.getCurrentPrice()) ? 0.0 : p.getCurrentPrice();
            
            totalRealizedPnL += realizedPnL;
            totalUnrealizedPnL += unrealizedPnL;
            totalValue += currentPrice * p.getQuantity();
        }

        List<PortfolioUpdate.PositionSummary> positionSummaries = positions.stream()
                .filter(p -> p.getQuantity() > 0)
                .map(p -> {
                    double avgPrice = Double.isNaN(p.getAveragePrice()) ? 0.0 : p.getAveragePrice();
                    double unrealizedPnL = Double.isNaN(p.getUnrealizedPnL()) ? 0.0 : p.getUnrealizedPnL();
                    double costBasis = avgPrice * p.getQuantity();
                    double unrealizedPnLPercent = costBasis > 0 ? (unrealizedPnL / costBasis) * 100 : 0.0;
                    
                    return new PortfolioUpdate.PositionSummary(
                        p.getSymbol(),
                        p.getQuantity(),
                        avgPrice,
                        p.getCurrentPrice(),
                        unrealizedPnL,
                        unrealizedPnLPercent
                    );
                })
                .collect(Collectors.toList());

        return new PortfolioUpdate(
            totalValue,
            totalRealizedPnL,
            totalUnrealizedPnL,
            totalRealizedPnL + totalUnrealizedPnL,
            positionSummaries,
            System.currentTimeMillis()
        );
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "positions", unless = "#result == null || #result.isEmpty()")
    public List<PositionDTO> getAllPositions() {
        return positionRepository.findAll().stream()
                .map(this::convertToPositionDTO)
                .collect(Collectors.toList());
    }

    public Page<TradeDTO> getAllTrades(Pageable pageable) {
        return tradeRepository.findAll(pageable)
                .map(this::convertToTradeDTO);
    }

    public Page<TradeDTO> getTradesBySymbol(String symbol, Pageable pageable) {
        return tradeRepository.findBySymbol(symbol, pageable)
                .map(this::convertToTradeDTO);
    }

    private PositionDTO convertToPositionDTO(Position position) {
        return new PositionDTO(
            position.getId(),
            position.getSymbol(),
            position.getQuantity(),
            Double.isNaN(position.getAveragePrice()) ? 0.0 : position.getAveragePrice(),
            Double.isNaN(position.getCurrentPrice()) ? 0.0 : position.getCurrentPrice(),
            Double.isNaN(position.getRealizedPnL()) ? 0.0 : position.getRealizedPnL(),
            Double.isNaN(position.getUnrealizedPnL()) ? 0.0 : position.getUnrealizedPnL(),
            position.getLastUpdated()
        );
    }

    private TradeDTO convertToTradeDTO(Trade trade) {
        return new TradeDTO(
            trade.getId(),
            trade.getTradeId(),
            trade.getBrokerOrderId(),
            trade.getSymbol(),
            trade.getSide(),
            trade.getPrice(),
            trade.getQuantity(),
            trade.getStatus(),
            trade.getTimestamp()
        );
    }
}
