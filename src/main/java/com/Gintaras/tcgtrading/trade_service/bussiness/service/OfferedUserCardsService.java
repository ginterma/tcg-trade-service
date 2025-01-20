package com.Gintaras.tcgtrading.trade_service.bussiness.service;

import com.Gintaras.tcgtrading.trade_service.model.OfferedUserCards;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;


public interface OfferedUserCardsService {

    ResponseEntity<OfferedUserCards> saveOfferedCards(OfferedUserCards offeredUserCards);

    public ResponseEntity<OfferedUserCards> getOfferedCardsById(Long id);

    public ResponseEntity<List<OfferedUserCards>> getOfferedCards();

    ResponseEntity<Void> deleteOfferedCardsById(Long id);
}
