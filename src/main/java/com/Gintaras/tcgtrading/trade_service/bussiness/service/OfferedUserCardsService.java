package com.Gintaras.tcgtrading.trade_service.bussiness.service;

import com.Gintaras.tcgtrading.trade_service.model.OfferedUserCards;

import java.util.List;
import java.util.Optional;


public interface OfferedUserCardsService {

    public OfferedUserCards saveOfferedCards(OfferedUserCards offeredUserCards);

    public Optional<OfferedUserCards> getOfferedCardsById(Long Id);

    public List<OfferedUserCards> getOfferedCards();

    public void deleteOfferedCardsById(Long id);
}
