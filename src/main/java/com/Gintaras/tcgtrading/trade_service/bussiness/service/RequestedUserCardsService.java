package com.Gintaras.tcgtrading.trade_service.bussiness.service;

import com.Gintaras.tcgtrading.trade_service.model.RequestedUserCards;

import java.util.List;
import java.util.Optional;

public interface RequestedUserCardsService {

    public RequestedUserCards saveRequestedCards(RequestedUserCards requestedUserCards);

    public Optional<RequestedUserCards> getRequestedCardsById(Long Id);

    public List<RequestedUserCards> getRequestedCards();

    public void deleteRequestedCardsById(Long id);
}
