package com.Gintaras.tcgtrading.trade_service.bussiness.repository;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.OfferedUserCardsDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfferedCardRepository extends JpaRepository<OfferedUserCardsDAO, Long> {

    @Modifying
    @Query("DELETE FROM OfferedUserCardsDAO o WHERE o.tradeDAO.id = :tradeId")
    void deleteByTradeId(@Param("tradeId") Long tradeId);
}
