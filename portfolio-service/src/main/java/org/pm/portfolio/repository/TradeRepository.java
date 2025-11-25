package org.pm.portfolio.repository;

import org.pm.portfolio.model.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    Page<Trade> findBySymbol(String symbol, Pageable pageable);
    Page<Trade> findByStatus(String status, Pageable pageable);
}
