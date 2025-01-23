package com.Gintaras.tcgtrading.trade_service.ControllerTest;

import com.Gintaras.tcgtrading.trade_service.bussiness.controller.OfferedUserCardsController;
import com.Gintaras.tcgtrading.trade_service.bussiness.controller.RatingController;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.OfferedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RatingDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.TradeDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.OfferedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RatingRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.TradeRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.OfferedUserCardsService;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.RatingService;
import com.Gintaras.tcgtrading.trade_service.mapper.OfferedUserCardsMapStruct;
import com.Gintaras.tcgtrading.trade_service.mapper.RatingMapStruct;
import com.Gintaras.tcgtrading.trade_service.model.OfferedUserCards;
import com.Gintaras.tcgtrading.trade_service.model.Rating;
import com.Gintaras.tcgtrading.trade_service.model.Trade;
import com.Gintaras.tcgtrading.trade_service.model.TradeStatus;
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

@WebMvcTest(OfferedUserCardsController.class)
public class OfferedUserCardsControllerTest {

    private final String OFFERED_URI = "/api/v1/offered/cards";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OfferedUserCardsService offeredUserCardsService;
    @MockitoBean
    private TradeRepository tradeRepository;
    @MockitoBean
    private OfferedCardRepository offeredCardRepository;
    @MockitoBean
    private OfferedUserCardsMapStruct offeredUserCardsMapStruct;


    private OfferedUserCards cards;
    private OfferedUserCardsDAO cardsDAO;
    private Trade trade;
    private TradeDAO tradeDAO;

    @BeforeEach
    public void setUp() {
        cards = new OfferedUserCards(1L, 1L, "1", 1);
        cardsDAO = new OfferedUserCardsDAO(1L, null, "1", 1);
        trade = new Trade(1L, "1L", "2L", new Date(), new Date(System.currentTimeMillis() + 1000000),
                TradeStatus.UNDERGOING, 500.0, 450.0, null, null, null);
        tradeDAO = new TradeDAO(1L, "1L", "2L", new Date(), new Date(System.currentTimeMillis() + 1000000),
                TradeStatus.UNDERGOING, 500.0, 450.0, null, null, null);
    }
    @Test
    public void deleteOfferedCardsControllerTest_WhenExist() throws Exception {
        when(offeredUserCardsService.getOfferedCardsById(1L)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cards));
        when(offeredCardRepository.findById(1L)).thenReturn(Optional.of(cardsDAO));
        when(offeredUserCardsService.deleteOfferedCardsById(1L)).thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).body(null));
        doNothing().when(offeredCardRepository).deleteByTradeId(1L);

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .delete(OFFERED_URI + "/1"))
                .andExpect(status().isNoContent());
    }
    @Test
    public void deleteOfferedCardsControllerTest_WhenNotExist() throws Exception {
        when(offeredUserCardsService.getOfferedCardsById(1L)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        when(offeredCardRepository.findById(1L)).thenReturn(Optional.empty());
        when(offeredUserCardsService.deleteOfferedCardsById(1L)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        doNothing().when(offeredCardRepository).deleteByTradeId(1L);

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .delete(OFFERED_URI + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getOfferedCardsByIdControllerTest_WhenExist() throws Exception {
        when(offeredUserCardsService.getOfferedCardsById(1L)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cards));
        when(offeredCardRepository.findById(1L)).thenReturn(Optional.of(cardsDAO));
        when(offeredUserCardsMapStruct.OfferedUserCardsDAOToOfferedUserCards(cardsDAO)).thenReturn(cards);

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(OFFERED_URI +  "/" + 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tradeId").value(1L))
                .andExpect(jsonPath("$.offeredCardId").value("1"))
                .andExpect(jsonPath("$.amount").value(1));

    }

    @Test
    public void getOfferedCardsByIdControllerTest_WhenNotExist() throws Exception {
        when(offeredUserCardsService.getOfferedCardsById(1L)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        when(offeredCardRepository.findById(1L)).thenReturn(Optional.empty());

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(OFFERED_URI +  "/" + 1L))
                .andExpect(status().isNotFound());

    }
    @Test
    public void getOfferedCardsListControllerTest() throws Exception {
        List<OfferedUserCards> cardList = new ArrayList<>();
        List<OfferedUserCardsDAO> cardDAOList = new ArrayList<>();
        cardList.add(cards);
        cardDAOList.add(cardsDAO);
        when(offeredUserCardsService.getOfferedCards()).thenReturn(ResponseEntity.status(HttpStatus.OK).body(cardList));
        when(offeredCardRepository.findAll()).thenReturn(cardDAOList);
        when(offeredUserCardsMapStruct.OfferedUserCardsDAOToOfferedUserCards(cardsDAO)).thenReturn(cards);

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(OFFERED_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].tradeId").value(1L))
                .andExpect(jsonPath("$[0].offeredCardId").value("1"))
                .andExpect(jsonPath("$[0].amount").value(1));
    }
}