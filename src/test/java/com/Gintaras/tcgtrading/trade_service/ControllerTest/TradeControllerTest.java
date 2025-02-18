package com.Gintaras.tcgtrading.trade_service.ControllerTest;

import com.Gintaras.tcgtrading.trade_service.bussiness.controller.TradeController;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.TradeDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.OfferedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RatingRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RequestedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.TradeRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.TradeService;
import com.Gintaras.tcgtrading.trade_service.mapper.TradeMapStruct;
import com.Gintaras.tcgtrading.trade_service.model.Trade;
import com.Gintaras.tcgtrading.trade_service.model.TradeStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

@WebMvcTest(TradeController.class)
public class TradeControllerTest {

    private final String TRADE_URI = "/api/v1/trade";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    TradeService tradeService;

    private Trade trade;
    private TradeDAO tradeDAO;

    @BeforeEach
    public void setUp(){
        trade = new Trade(1L, "1L", "2L", new Date(), new Date(System.currentTimeMillis() + 1000000),
                TradeStatus.UNDERGOING, 500.0, 450.0, null, null, null);
        tradeDAO = new TradeDAO(1L, "1L", "2L", new Date(), new Date(System.currentTimeMillis() + 1000000),
                TradeStatus.UNDERGOING, 500.0, 450.0, null, null, null);
    }

//    @Test
//    public void saveTradeTest_WhenTradeExist() throws Exception {
//        when(tradeService.saveTrade(trade)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(trade));
//        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
//                        .post(TRADE_URI)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(JsonString(trade))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isCreated())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(1L))
//                .andExpect(jsonPath("$.requesterId").value("1L"))
//                .andExpect(jsonPath("$.offereeId").value("2L"));
//    }

    @Test
    public void deleteTradeControllerTest_WhenTradeExist() throws Exception {
        when(tradeService.getTradeById(1L)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(trade));
        when(tradeService.deleteTradeById(1L)).thenReturn(ResponseEntity.noContent().build());
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .delete(TRADE_URI + "/1"))
                .andExpect(status().isNoContent());
    }
    @Test
    public void deleteTradeControllerTest_WhenTradeNotExist() throws Exception {
        when(tradeService.getTradeById(1L)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .delete(TRADE_URI + "/1"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void getTradeListTest() throws Exception {
        List<TradeDAO> tradeDAOList = new ArrayList<>();
        tradeDAOList.add(tradeDAO);
        List<Trade> tradeList = new ArrayList<>();
        tradeList.add(trade);
        when(tradeService.getTradeList()).thenReturn(ResponseEntity.status(HttpStatus.OK).body(tradeList));
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(TRADE_URI))
                .andExpect(status().isOk());
    }
    @Test
    public void getTradeByIdTest_WhenTradeExist() throws Exception{
        when(tradeService.getTradeById(1L)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(trade));
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(TRADE_URI +  "/" + 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requesterId").value("1L"))
                .andExpect(jsonPath("$.offereeId").value("2L"));

    }
    @Test
    public void getTradeByIdTest_WhenTradeNotExist() throws Exception{
        when(tradeService.getTradeById(1L)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(TRADE_URI +  "/" + 1L))
                .andExpect(status().isNotFound());

    }


    private static String JsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
