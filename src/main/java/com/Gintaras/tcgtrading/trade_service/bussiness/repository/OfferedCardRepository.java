package com.Gintaras.tcgtrading.trade_service.bussiness.repository;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.OfferedUserCardDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferedCardRepository extends JpaRepository<OfferedUserCardDAO, Long> {
}
