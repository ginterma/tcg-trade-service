package com.Gintaras.tcgtrading.trade_service.bussiness.repository;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RatingDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<RatingDAO, Long> {
    @Modifying
    @Query("DELETE FROM RatingDAO o WHERE o.tradeDAO.id = :tradeId")
    void deleteByTradeId(@Param("tradeId") Long tradeId);
}
