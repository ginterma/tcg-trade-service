package com.Gintaras.tcgtrading.trade_service.bussiness.repository;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RequestedUserCardDAO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestedCardRepository extends JpaRepository<RequestedUserCardDAO, Long> {
}
