package com.Gintaras.tcgtrading.trade_service.ControllerTest;

import com.Gintaras.tcgtrading.trade_service.bussiness.controller.RequestedUserCardsController;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RequestedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.TradeDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RequestedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.TradeRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.RequestedUserCardsService;
import com.Gintaras.tcgtrading.trade_service.mapper.RequestedUserCardsMapStruct;
import com.Gintaras.tcgtrading.trade_service.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(RequestedUserCardsController.class)
public class RequestedUserCardsControllerTest {

    private final String REQUESTED_URI = "/api/v1/requested/cards";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RequestedUserCardsService requestedUserCardsService;
    @MockitoBean
    private TradeRepository tradeRepository;
    @MockitoBean
    private RequestedCardRepository requestedCardRepository;
    @MockitoBean
    private RequestedUserCardsMapStruct requestedUserCardsMapStruct;


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
    public void deleteRequestedCardsControllerTest_WhenExist() throws Exception {
        when(requestedUserCardsService.getRequestedCardsById(1L)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cards));
        when(requestedCardRepository.findById(1L)).thenReturn(Optional.of(cardsDAO));
        when(requestedUserCardsService.deleteRequestedCardsById(1L)).thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).body(null));
        doNothing().when(requestedCardRepository).deleteByTradeId(1L);

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .delete(REQUESTED_URI + "/1"))
                .andExpect(status().isNoContent());
    }
    @Test
    public void deleteRequestedCardsControllerTest_WhenNotExist() throws Exception {
        when(requestedUserCardsService.getRequestedCardsById(1L)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        when(requestedCardRepository.findById(1L)).thenReturn(Optional.empty());
        when(requestedUserCardsService.deleteRequestedCardsById(1L)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        doNothing().when(requestedCardRepository).deleteByTradeId(1L);

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .delete(REQUESTED_URI + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getRequestedCardsByIdControllerTest_WhenExist() throws Exception {
        when(requestedUserCardsService.getRequestedCardsById(1L)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cards));
        when(requestedCardRepository.findById(1L)).thenReturn(Optional.of(cardsDAO));
        when(requestedUserCardsMapStruct.RequestedUserCardsDAOToRequestedUserCards(cardsDAO)).thenReturn(cards);

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(REQUESTED_URI +  "/" + 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tradeId").value(1L))
                .andExpect(jsonPath("$.requestedCardId").value("1"))
                .andExpect(jsonPath("$.amount").value(1));

    }

    @Test
    public void getRequestedCardsByIdControllerTest_WhenNotExist() throws Exception {
        when(requestedUserCardsService.getRequestedCardsById(1L)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        when(requestedCardRepository.findById(1L)).thenReturn(Optional.empty());

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(REQUESTED_URI +  "/" + 1L))
                .andExpect(status().isNotFound());

    }
    @Test
    public void getRequestedCardsListControllerTest() throws Exception {
        List<RequestedUserCards> cardList = new ArrayList<>();
        List<RequestedUserCardsDAO> cardDAOList = new ArrayList<>();
        cardList.add(cards);
        cardDAOList.add(cardsDAO);
        when(requestedUserCardsService.getRequestedCards()).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardList));
        when(requestedCardRepository.findAll()).thenReturn(cardDAOList);
        when(requestedUserCardsMapStruct.RequestedUserCardsDAOToRequestedUserCards(cardsDAO)).thenReturn(cards);

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(REQUESTED_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].tradeId").value(1L))
                .andExpect(jsonPath("$[0].requestedCardId").value("1"))
                .andExpect(jsonPath("$[0].amount").value(1));
    }
}