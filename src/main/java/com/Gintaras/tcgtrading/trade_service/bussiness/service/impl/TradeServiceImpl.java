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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class TradeServiceImpl implements TradeService {


    private final TradeRepository tradeRepository;
    private final OfferedCardRepository offeredCardRepository;
    private final RequestedCardRepository requestedCardRepository;
    private final RatingRepository ratingRepository;
    private final TradeMapStruct tradeMapper;
    private final WebClient userClient;
    private final WebClient userCardClient;


    public TradeServiceImpl(TradeRepository tradeRepository, OfferedCardRepository offeredCardRepository, RequestedCardRepository requestedCardRepository, RatingRepository ratingRepository, TradeMapStruct tradeMapper, @Qualifier("user") WebClient userClient,
                            @Qualifier("usercard") WebClient userCardClient) {
        this.tradeRepository = tradeRepository;
        this.offeredCardRepository = offeredCardRepository;
        this.requestedCardRepository = requestedCardRepository;
        this.ratingRepository = ratingRepository;
        this.tradeMapper = tradeMapper;
        this.userClient = userClient;
        this.userCardClient = userCardClient;
    }

    @Override
    public ResponseEntity<Trade> saveTrade(Trade trade) {
        HttpStatus offereeStatus = (HttpStatus) userClient.get()
                .uri("/{id}", trade.getOffereeId())
                .retrieve()
                .toBodilessEntity()
                .map(ResponseEntity::getStatusCode)
                .block();
        HttpStatus requesterStatus = (HttpStatus) userClient.get()
                .uri("/{id}", trade.getRequesterId())
                .retrieve()
                .toBodilessEntity()
                .map(ResponseEntity::getStatusCode)
                .block();

        if (!offereeStatus.is2xxSuccessful() || !requesterStatus.is2xxSuccessful()) {
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
        if (!checkIfTradeUndergoing(existingTrade)) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        List<OfferedUserCardsDAO> offeredCardList = existingTrade.getOfferedCardList();
        List<RequestedUserCardsDAO> requestedCardList = existingTrade.getRequestedCardList();

        for (OfferedUserCardsDAO offeredUserCardsDAO : offeredCardList) {
            Integer amount = fetchCardAmount(offeredUserCardsDAO.getOfferedCardId());
            if (!checkIfEnoughCards(amount, offeredUserCardsDAO.getAmount())) {
                return badRequestResponse();
            }

            processCards(amount, offeredUserCardsDAO.getAmount(),
                    offeredUserCardsDAO.getOfferedCardId(), existingTrade.getOffereeId(),existingTrade.getRequesterId());
        }
        for (RequestedUserCardsDAO requestedUserCardsDAO : requestedCardList) {
            Integer amount = fetchCardAmount(requestedUserCardsDAO.getRequestedCardId());
            if (!checkIfEnoughCards(amount, requestedUserCardsDAO.getAmount())) {
                return badRequestResponse();
            }
            processCards(amount, requestedUserCardsDAO.getAmount(),
                    requestedUserCardsDAO.getRequestedCardId(), existingTrade.getRequesterId(),existingTrade.getOffereeId());
        }


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
    @Transactional
    private void processCards(Integer currentAmount, Integer requestedAmount, String userCardId, String userId1, String userId2) {
        userCardClient.put()
                .uri("/amount/{id}", userCardId)
                .bodyValue(currentAmount - requestedAmount)  // Pass the integer directly
                .retrieve()
                .toBodilessEntity()
                .block();

        String cardId = userCardClient.get()
                .uri("/card/{id}",userCardId)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        String user2CardId = userCardClient.get()
                .uri("/unique/{userId}/{cardId}", userId2, cardId)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        Integer amountAfter = requestedAmount;
        if (user2CardId != null) {
            Integer amountBefore = userCardClient.get()
                    .uri("/amount/{id}", user2CardId)
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .block();
            amountAfter = amountAfter + amountBefore;
        }


        String jsonString = String.format("{\"userId\": \"%s\", \"cardId\": \"%s\", \"amount\": %d}", userId2, cardId, amountAfter);
        userCardClient.post()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(jsonString)
                .retrieve()
                .toBodilessEntity()
                .block();
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
        try {
            return userCardClient.get()
                    .uri("/amount/{id}", cardId)
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .block();
        } catch (Exception e) {
            log.error("Error fetching card amount for cardId: {}", cardId, e);
            return 0;
        }
    }

}
