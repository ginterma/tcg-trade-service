package com.Gintaras.tcgtrading.trade_service.bussiness.repository;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.TradeDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<TradeDAO, Long> {
}
