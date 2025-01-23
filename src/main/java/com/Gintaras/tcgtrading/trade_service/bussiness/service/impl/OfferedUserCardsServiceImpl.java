package com.Gintaras.tcgtrading.trade_service.bussiness.service.impl;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.OfferedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.OfferedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.TradeRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.OfferedUserCardsService;
import com.Gintaras.tcgtrading.trade_service.mapper.OfferedUserCardsMapStruct;
import com.Gintaras.tcgtrading.trade_service.model.OfferedUserCards;
import com.Gintaras.tcgtrading.trade_service.model.Trade;
import com.Gintaras.tcgtrading.trade_service.model.TradeStatus;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class OfferedUserCardsServiceImpl implements OfferedUserCardsService {

    @Autowired
    OfferedCardRepository offeredCardsRepository;
    @Autowired
    OfferedUserCardsMapStruct offeredCardsMapper;
    @Autowired
    TradeRepository tradeRepository;
    @Autowired
    TradeServiceImpl tradeService;

    private final RestClient restClient;
    public OfferedUserCardsServiceImpl() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:8082/api/v1/user/card").build();
    }

    @Transactional
    @Override
    public ResponseEntity<OfferedUserCards> saveOfferedCards(OfferedUserCards offeredUserCards) {
        ResponseEntity<Trade> responseEntity = tradeService.getTradeById(offeredUserCards.getTradeId());
        if (responseEntity.getBody() == null) {
            log.info("Trade with id {} does not exist", offeredUserCards.getTradeId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Trade trade = responseEntity.getBody();

        Optional<?> offeredCardExist = restClient.get().uri("/{id}", offeredUserCards.getOfferedCardId())
                .retrieve().body(Optional.class);
        if (offeredCardExist.isEmpty()) {
            log.warn("User card with Id {} does not exist", offeredUserCards.getOfferedCardId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        if (trade.getTradeStatus() == TradeStatus.COMPLETED || trade.getTradeStatus() == TradeStatus.CANCELED) {
            log.warn("Trade is already completed or canceled. Further adjustments are not allowed.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        OfferedUserCardsDAO offeredUserCardsDAO = offeredCardsRepository.save(offeredCardsMapper
                .OfferedCardsToOfferedCardsDAO(offeredUserCards));
        log.info("New Offered User Cards are saved: {}", offeredUserCards);

        Double offeredValue = restClient.get().uri("/value/{id}", offeredUserCards.getOfferedCardId())
                .retrieve().body(Double.class);
        offeredValue = offeredValue * offeredUserCards.getAmount();

        trade.setOfferedCardsValue(trade.getOfferedCardsValue() + offeredValue);
        tradeService.updateTrade(trade.getId(), trade);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(offeredCardsMapper.OfferedUserCardsDAOToOfferedUserCards(offeredUserCardsDAO));
    }

    @Override
    public ResponseEntity<Void> deleteOfferedCardsById(Long id) {
        if (!offeredCardsRepository.existsById(id)) {
            log.warn("Offered User Cards with id {} does not exist", id);
            return ResponseEntity.notFound().build();
        }

        offeredCardsRepository.deleteById(id);
        log.info("Offered User Cards with id {} have been deleted", id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<OfferedUserCards> getOfferedCardsById(Long id) {
        Optional<OfferedUserCards> offeredUserCards = offeredCardsRepository.findById(id)
                .map(offeredCardsMapper::OfferedUserCardsDAOToOfferedUserCards);

        if (offeredUserCards.isEmpty()) {
            log.info("Offered User Cards with id {} not found", id);
            return ResponseEntity.notFound().build();
        }

        log.info("Offered User Cards with id {} found: {}", id, offeredUserCards.get());
        return ResponseEntity.ok(offeredUserCards.get());
    }

    @Override
    public ResponseEntity<List<OfferedUserCards>> getOfferedCards() {
        List<OfferedUserCardsDAO> offeredCardList = offeredCardsRepository.findAll();

        log.info("Retrieved offered cards list. Size: {}", offeredCardList.size());
        List<OfferedUserCards> offeredUserCardsList = offeredCardList.stream()
                .map(offeredCardsMapper::OfferedUserCardsDAOToOfferedUserCards)
                .collect(Collectors.toList());

        return ResponseEntity.ok(offeredUserCardsList);
    }
}


