package com.Gintaras.tcgtrading.trade_service.bussiness.repository;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RatingDAO;
import com.Gintaras.tcgtrading.trade_service.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RatingRepository extends JpaRepository<RatingDAO, Long> {
    @Modifying
    @Query("DELETE FROM RatingDAO o WHERE o.tradeDAO.id = :tradeId")
    void deleteByTradeId(@Param("tradeId") Long tradeId);
    @Modifying
    @Query("SELECT o FROM RatingDAO o WHERE o.userId = :userId")
    List<RatingDAO> selectByUserId(@Param("userId") String userId);
}
