package com.Gintaras.tcgtrading.trade_service.bussiness.service;

import com.Gintaras.tcgtrading.trade_service.TradeServiceApplication;
import com.Gintaras.tcgtrading.trade_service.model.Trade;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface TradeService {

    public ResponseEntity<Trade> saveTrade(Trade trade);

    public ResponseEntity<Trade> getTradeById(Long Id);

    public ResponseEntity<List<Trade>> getTradeList();

    public ResponseEntity<Void> deleteTradeById(Long tradeId);

    ResponseEntity<Trade> updateTrade(Long tradeId, Trade updatedTrade);

    public ResponseEntity<Trade> completeTrade(Long tradeId);
}
