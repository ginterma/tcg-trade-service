package com.Gintaras.tcgtrading.trade_service.bussiness.service.impl;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.OfferedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RequestedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.TradeDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.OfferedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RatingRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RequestedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.TradeRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.TradeService;
import com.Gintaras.tcgtrading.trade_service.mapper.TradeMapStruct;
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
public class TradeServiceImpl implements TradeService {


    @Autowired
    TradeRepository tradeRepository;
    @Autowired
    OfferedCardRepository offeredCardRepository;
    @Autowired
    RequestedCardRepository requestedCardRepository;
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    TradeMapStruct tradeMapper;
    private final RestClient restClient;
    private final RestClient restClientUserCard;

    private final String USER_URI = "http://localhost:9098/api/v1/user";
    private final String CARD_URI = "http://localhost:8082/api/v1/user/card";

    public TradeServiceImpl() {
        restClient = RestClient.builder().baseUrl(USER_URI).build();
        restClientUserCard = RestClient.builder().baseUrl(CARD_URI).build();
    }

    @Override
    public ResponseEntity<Trade> saveTrade(Trade trade) {
        Optional<?> offereeExists = restClient.get().uri("/{id}",
                trade.getOffereeId()).retrieve().body(Optional.class);
        Optional<?> requesterExists = restClient.get().uri("/{id}",
                trade.getRequesterId()).retrieve().body(Optional.class);
        if (offereeExists.isEmpty() || requesterExists.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        trade.setTradeStatus(TradeStatus.UNDERGOING);

        TradeDAO tradeDAO = tradeRepository.save(tradeMapper.TradeToTradeDAO(trade));
        log.info("New Trade is saved: {}", trade);
        Trade savedTrade = tradeMapper.TradeDAOToTrade(tradeDAO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTrade);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> deleteTradeById(Long tradeId) {
        Optional<TradeDAO> existingTradeOptional = tradeRepository.findById(tradeId);
        if (existingTradeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ratingRepository.deleteByTradeId(tradeId);
        requestedCardRepository.deleteByTradeId(tradeId);
        offeredCardRepository.deleteByTradeId(tradeId);
        tradeRepository.deleteById(tradeId);

        log.info("Trade with id {} has been deleted. " +
                "Offered and Requested Cards Records were also deleted. Trade ratings deleted.", tradeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<Trade> getTradeById(Long id) {
        Optional<TradeDAO> tradeDAOOptional = tradeRepository.findById(id);
        if (tradeDAOOptional.isEmpty()) {
            log.warn("Trade with id {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Trade trade = tradeMapper.TradeDAOToTrade(tradeDAOOptional.get());
        log.info("Trade with id {} is {}", id, trade);
        return ResponseEntity.status(HttpStatus.OK).body(trade);
    }

    @Override
    public ResponseEntity<List<Trade>> getTradeList() {
        List<TradeDAO> tradeList = tradeRepository.findAll();
        if (tradeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        List<Trade> trades = tradeList.stream().map(tradeMapper::TradeDAOToTrade).collect(Collectors.toList());
        log.info("Get trade list. Size is: {}", trades.size());
        return ResponseEntity.status(HttpStatus.OK).body(trades);
    }

    @Override
    public ResponseEntity<Trade> updateTrade(Long tradeId, Trade updatedTrade) {
        Optional<TradeDAO> existingTradeOptional = tradeRepository.findById(tradeId);
        if (existingTradeOptional.isEmpty()) {
            log.warn("Trade with id {} does not exist", tradeId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        TradeDAO existingTrade = existingTradeOptional.get();
        if (!TradeStatus.UNDERGOING.equals(existingTrade.getTradeStatus())) {
            log.warn("Trade with id {} cannot be updated as its status is not UNDERGOING", tradeId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        updatedTrade.setId(tradeId);
        updatedTrade.setTradeStatus(existingTrade.getTradeStatus()); // Preserve the existing status
        TradeDAO updatedTradeDAO = tradeRepository.save(tradeMapper.TradeToTradeDAO(updatedTrade));

        log.info("Trade with id {} has been updated: {}", tradeId, updatedTrade);
        Trade savedUpdatedTrade = tradeMapper.TradeDAOToTrade(updatedTradeDAO);
        return ResponseEntity.status(HttpStatus.OK).body(savedUpdatedTrade);
    }

    @Override
    @Transactional
    public ResponseEntity<Trade> completeTrade(Long tradeId) {
        Optional<TradeDAO> existingTradeOptional = tradeRepository.findById(tradeId);
        if (existingTradeOptional.isEmpty()) {
            return notFoundResponse();
        }
        TradeDAO existingTrade = existingTradeOptional.get();
        List<OfferedUserCardsDAO> offeredCardList = existingTrade.getOfferedCardList();
        List<RequestedUserCardsDAO> requestedCardList = existingTrade.getRequestedCardList();

        for (OfferedUserCardsDAO offeredUserCardsDAO : offeredCardList) {
            Integer amount = fetchCardAmount(offeredUserCardsDAO.getOfferedCardId());
            if (!checkIfEnoughCards(offeredUserCardsDAO.getAmount(), amount)) {
                return badRequestResponse();
            }

            processCards(offeredUserCardsDAO.getAmount(), amount,
                    offeredUserCardsDAO.getOfferedCardId(), existingTrade.getOffereeId());
        }
        for (RequestedUserCardsDAO requestedUserCardsDAO : requestedCardList) {
            Integer amount = fetchCardAmount(requestedUserCardsDAO.getRequestedCardId());
            if (!checkIfEnoughCards(requestedUserCardsDAO.getAmount(), amount)) {
                return badRequestResponse();
            }
            processCards(requestedUserCardsDAO.getAmount(), amount,
                    requestedUserCardsDAO.getRequestedCardId(), existingTrade.getRequesterId());
        }

        if (!checkIfTradeUndergoing(existingTrade)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        existingTrade.setTradeStatus(TradeStatus.COMPLETED);
        TradeDAO updatedTradeDAO = tradeRepository.save(existingTrade);

        Trade updatedTrade = tradeMapper.TradeDAOToTrade(updatedTradeDAO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedTrade);
    }


    private boolean checkIfEnoughCards(Integer currentAmount, Integer requestedAmount) {
        if (currentAmount - requestedAmount < 0) {
            log.warn("User has not enough cards to complete the trade. Trade cannot be completed.");
            return false;
        }
        return true;
    }

    private boolean checkIfTradeUndergoing(TradeDAO trade) {
        return TradeStatus.UNDERGOING.equals(trade.getTradeStatus());
    }

    private void processCards(Integer currentAmount, Integer requestedAmount, String userCardId, String userId) {
        String requestBody = String.format("%d", currentAmount - requestedAmount);
        restClientUserCard.put().uri("/amount/{id}", userCardId).body(requestBody);
        ResponseEntity<?> getUserCardId = restClientUserCard.get().uri("/unique/{userId}/{cardId}", userId, userCardId).retrieve().body(ResponseEntity.class);
        Integer amountAfter = requestedAmount;
        if (getUserCardId != ResponseEntity.notFound().build()) {
            Integer amountBefore = restClientUserCard.get().uri("/amount/{id}", userCardId).retrieve().body(Integer.class);
            amountAfter += amountBefore;
        }
        String jsonString = String.format("{\"userId\": \"%s\", \"cardId\": \"%s\", \"amount\": %d}", userId, userCardId, amountAfter);
        restClientUserCard.post().body(jsonString);
    }

    private ResponseEntity<Trade> notFoundResponse() {
        log.warn("Trade does not exist");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    private ResponseEntity<Trade> badRequestResponse() {
        log.warn("User does not have enough cards to complete the trade.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    private Integer fetchCardAmount(String cardId) {
        return restClientUserCard.get().uri("/amount/{id}", cardId).retrieve().body(Integer.class);
    }

}
