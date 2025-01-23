package com.Gintaras.tcgtrading.trade_service.ServiceTest;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.OfferedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RequestedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.TradeDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.OfferedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RequestedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.TradeRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.impl.OfferedUserCardsServiceImpl;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.impl.RequestedUserCardsServiceImpl;
import com.Gintaras.tcgtrading.trade_service.mapper.OfferedUserCardsMapStruct;
import com.Gintaras.tcgtrading.trade_service.mapper.RequestedUserCardsMapStruct;
import com.Gintaras.tcgtrading.trade_service.model.OfferedUserCards;
import com.Gintaras.tcgtrading.trade_service.model.RequestedUserCards;
import com.Gintaras.tcgtrading.trade_service.model.Trade;
import com.Gintaras.tcgtrading.trade_service.model.TradeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RequestedUserCardsServiceTest {

    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private RequestedCardRepository cardRepository;
    @Mock
    private RequestedUserCardsMapStruct cardsMapStruct;


    @InjectMocks
    private RequestedUserCardsServiceImpl cardsService;

    private RequestedUserCards cards;
    private RequestedUserCardsDAO cardsDAO;
    private Trade trade;
    private TradeDAO tradeDAO;

    @BeforeEach
    public void setUp() {
        cards = new RequestedUserCards(1L, 1L, "1", 1);
        cardsDAO = new RequestedUserCardsDAO(1L, null, "1", 1);
        trade = new Trade(1L, "1L", "2L", new Date(), new Date(System.currentTimeMillis() + 1000000),
                TradeStatus.UNDERGOING, 500.0, 450.0, null, null, null);
        tradeDAO = new TradeDAO(1L, "1L", "2L", new Date(), new Date(System.currentTimeMillis() + 1000000),
                TradeStatus.UNDERGOING, 500.0, 450.0, null, null, null);

    }
    @Test
    public void deleteRequestedCardsTest_RequestExist(){
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardsDAO));
        doNothing().when(cardRepository).deleteById(1L);

        ResponseEntity<Void> responseEntity = cardsService.deleteRequestedCardsById(1L);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals(null, responseEntity.getBody());
    }
    @Test
    public void deleteRequestedCardsTest_RequestNotExist(){
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Void> responseEntity = cardsService.deleteRequestedCardsById(1L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
    @Test
    public void getRequestedCardsByIdTest_CardsExist(){
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardsDAO));
        when(cardsMapStruct.RequestedUserCardsDAOToRequestedUserCards(cardsDAO)).thenReturn(cards);

        ResponseEntity<RequestedUserCards> responseEntity = cardsService.getRequestedCardsById(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(cards, responseEntity.getBody());
    }

    @Test
    public void getRequestedCardsByIdTest_CardsNotExist(){
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<RequestedUserCards> responseEntity = cardsService.getRequestedCardsById(1L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void getRequestedCardsTest(){
        List<RequestedUserCards> cardList = new ArrayList<>();
        List<RequestedUserCardsDAO> cardDAOList = new ArrayList<>();
        cardList.add(cards);
        cardDAOList.add(cardsDAO);
        when(cardRepository.findAll()).thenReturn(cardDAOList);
        when(cardsMapStruct.RequestedUserCardsDAOToRequestedUserCards(cardsDAO)).thenReturn(cards);

        ResponseEntity<List<RequestedUserCards>> responseEntity = cardsService.getRequestedCards();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(cardList, responseEntity.getBody());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());

    }
}