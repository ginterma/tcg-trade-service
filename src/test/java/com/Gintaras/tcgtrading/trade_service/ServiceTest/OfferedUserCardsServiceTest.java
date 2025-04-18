package com.Gintaras.tcgtrading.trade_service.ServiceTest;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.OfferedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.OfferedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.impl.OfferedUserCardsServiceImpl;
import com.Gintaras.tcgtrading.trade_service.mapper.OfferedUserCardsMapStruct;
import com.Gintaras.tcgtrading.trade_service.model.OfferedUserCards;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OfferedUserCardsServiceTest {

    @Mock
    private OfferedCardRepository cardRepository;
    @Mock
    private OfferedUserCardsMapStruct cardsMapStruct;

    @InjectMocks
    private OfferedUserCardsServiceImpl cardsService;

    private OfferedUserCards cards;
    private OfferedUserCardsDAO cardsDAO;


    @BeforeEach
    public void setUp() {
        cards = new OfferedUserCards(1L, 1L, "1", 1);
        cardsDAO = new OfferedUserCardsDAO(1L, null, "1", 1);
    }
    @Test
    public void deleteOfferedCardsTest_OfferExist(){
        when(cardRepository.existsById(1L)).thenReturn(true);
        doNothing().when(cardRepository).deleteById(1L);

        ResponseEntity<Void> responseEntity = cardsService.deleteOfferedCardsById(1L);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals(null, responseEntity.getBody());
    }
    @Test
    public void deleteOfferedCardsTest_OfferNotExist(){
        when(cardRepository.existsById(1L)).thenReturn(false);

        ResponseEntity<Void> responseEntity = cardsService.deleteOfferedCardsById(1L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(null, responseEntity.getBody());
    }
    @Test
    public void getOfferedCardsByIdTest_CardsExist(){
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardsDAO));
        when(cardsMapStruct.OfferedUserCardsDAOToOfferedUserCards(cardsDAO)).thenReturn(cards);

        ResponseEntity<OfferedUserCards> responseEntity = cardsService.getOfferedCardsById(1L);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(cards, responseEntity.getBody());
    }

    @Test
    public void getOfferedCardsByIdTest_CardsNotExist(){
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<OfferedUserCards> responseEntity = cardsService.getOfferedCardsById(1L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void getOfferedCardsTest(){
        List<OfferedUserCards> cardList = new ArrayList<>();
        List<OfferedUserCardsDAO> cardDAOList = new ArrayList<>();
        cardList.add(cards);
        cardDAOList.add(cardsDAO);
        when(cardRepository.findAll()).thenReturn(cardDAOList);
        when(cardsMapStruct.OfferedUserCardsDAOToOfferedUserCards(cardsDAO)).thenReturn(cards);

        ResponseEntity<List<OfferedUserCards>> responseEntity = cardsService.getOfferedCards();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(cardList, responseEntity.getBody());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).size());

    }
}