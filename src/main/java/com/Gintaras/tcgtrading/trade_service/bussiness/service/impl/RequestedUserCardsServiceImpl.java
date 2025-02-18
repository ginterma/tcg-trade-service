package com.Gintaras.tcgtrading.trade_service.bussiness.service.impl;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RequestedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RequestedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.RequestedUserCardsService;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.TradeService;
import com.Gintaras.tcgtrading.trade_service.mapper.RequestedUserCardsMapStruct;
import com.Gintaras.tcgtrading.trade_service.model.RequestedUserCards;
import com.Gintaras.tcgtrading.trade_service.model.Trade;
import com.Gintaras.tcgtrading.trade_service.model.TradeStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RequestedUserCardsServiceImpl implements RequestedUserCardsService {


    private final RequestedCardRepository requestedCardRepository;
    private final RequestedUserCardsMapStruct requestedCardsMapper;
    private final TradeService tradeService;
    private final WebClient webUserCardClient;

    public RequestedUserCardsServiceImpl(RequestedCardRepository requestedCardRepository,
                                         RequestedUserCardsMapStruct requestedCardsMapper, TradeService tradeService,
                                         @Qualifier("usercard") WebClient webUserCardClient) {
        this.requestedCardRepository = requestedCardRepository;
        this.requestedCardsMapper = requestedCardsMapper;
        this.tradeService = tradeService;
        this.webUserCardClient = webUserCardClient;
    }


    @Override
    public ResponseEntity<RequestedUserCards> saveRequestedCards(RequestedUserCards requestedUserCards) {
        ResponseEntity<Trade> tradeResponse = tradeService.getTradeById(requestedUserCards.getTradeId());
        if (!tradeResponse.getStatusCode().is2xxSuccessful() || tradeResponse.getBody() == null) {
            return ResponseEntity.status(404).body(null);
        }

        ResponseEntity<Void> requestedCardExist;
        try {
            requestedCardExist = webUserCardClient.get().uri("/{id}", requestedUserCards.getRequestedCardId())
                    .retrieve().toBodilessEntity().block();
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(404).body(null);
        }

        if (!requestedCardExist.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(404).body(null);
        }

        if (tradeResponse.getBody().getTradeStatus() == TradeStatus.COMPLETED || tradeResponse.getBody().getTradeStatus() == TradeStatus.CANCELED) {
            return ResponseEntity.status(400).body(null);
        }

        RequestedUserCardsDAO requestedUserCardsDAO = requestedCardRepository.save(requestedCardsMapper
                .RequestedCardsToRequestedCardsDAO(requestedUserCards));

        Double offeredValue;
        try {
            offeredValue = webUserCardClient.get().uri("/value/{id}", requestedUserCards.getRequestedCardId())
                    .retrieve().bodyToMono(Double.class).block();
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(500).body(null);
        }

        offeredValue = offeredValue * requestedUserCards.getAmount();
        tradeResponse.getBody().setRequestedCardsValue(tradeResponse.getBody().getRequestedCardsValue() + offeredValue);
        ResponseEntity<Trade> updatedTradeResponse = tradeService.updateTrade(tradeResponse.getBody().getId(), tradeResponse.getBody());

        if (!updatedTradeResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(500).body(null);
        }

        return ResponseEntity.status(201).body(requestedCardsMapper.RequestedUserCardsDAOToRequestedUserCards(requestedUserCardsDAO));
    }

    @Override
    public ResponseEntity<Void> deleteRequestedCardsById(Long id) {
        Optional<RequestedUserCardsDAO> requestedUserCards = requestedCardRepository.findById(id);
        if (requestedUserCards.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        requestedCardRepository.deleteById(id);
        return ResponseEntity.status(204).build();
    }

    @Override
    public ResponseEntity<RequestedUserCards> getRequestedCardsById(Long id) {
        Optional<RequestedUserCards> requestedUserCards = requestedCardRepository.findById(id)
                .map(requestedCardsMapper::RequestedUserCardsDAOToRequestedUserCards);

        if (requestedUserCards.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        return ResponseEntity.ok(requestedUserCards.get());
    }

    @Override
    public ResponseEntity<List<RequestedUserCards>> getRequestedCards() {
        List<RequestedUserCardsDAO> requestedCardList = requestedCardRepository.findAll();
        if (requestedCardList.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }

        List<RequestedUserCards> responseList = requestedCardList.stream()
                .map(requestedCardsMapper::RequestedUserCardsDAOToRequestedUserCards)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }
}
