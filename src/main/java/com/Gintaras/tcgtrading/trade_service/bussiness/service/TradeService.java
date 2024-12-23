package com.Gintaras.tcgtrading.trade_service.bussiness.service;

import com.Gintaras.tcgtrading.trade_service.TradeServiceApplication;
import com.Gintaras.tcgtrading.trade_service.model.Trade;

import java.util.List;
import java.util.Optional;

public interface TradeService {

    public Trade saveTrade(Trade trade);

    public Optional<Trade> getTradeById(Long Id);

    public List<Trade> getTradeList();

    public void deleteTradeById(Long tradeId);
}
