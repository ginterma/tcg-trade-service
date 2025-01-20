package com.Gintaras.tcgtrading.trade_service.bussiness.service;

import com.Gintaras.tcgtrading.trade_service.model.RequestedUserCards;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface RequestedUserCardsService {

    ResponseEntity<RequestedUserCards> saveRequestedCards(RequestedUserCards requestedUserCards);

    ResponseEntity<RequestedUserCards> getRequestedCardsById(Long Id);

    ResponseEntity<List<RequestedUserCards>> getRequestedCards();

    ResponseEntity<Void> deleteRequestedCardsById(Long id);
}
