package com.Gintaras.tcgtrading.trade_service.ServiceTest;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.TradeDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.OfferedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RatingRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RequestedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.TradeRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.TradeService;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.impl.TradeServiceImpl;
import com.Gintaras.tcgtrading.trade_service.mapper.TradeMapStruct;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private OfferedCardRepository offeredCardRepository;
    @Mock
    private RequestedCardRepository requestedCardRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    TradeMapStruct tradeMapStruct;


    @InjectMocks
    private TradeServiceImpl tradeServiceImpl;

    private Trade trade;
    private TradeDAO tradeDAO;

    @BeforeEach
    public void setUp(){
        trade = new Trade(1L, "1L", "2L", new Date(), new Date(System.currentTimeMillis() + 1000000),
                TradeStatus.UNDERGOING, 500.0, 450.0, null, null, null);
         tradeDAO = new TradeDAO(1L, "1L", "2L", new Date(), new Date(System.currentTimeMillis() + 1000000),
                TradeStatus.UNDERGOING, 500.0, 450.0, null, null, null);
    }
    @Test
    public void deleteTradeTest_WhenIdExists(){
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(tradeDAO));
        doNothing().when(offeredCardRepository).deleteByTradeId(1L);
        doNothing().when(requestedCardRepository).deleteByTradeId(1L);
        doNothing().when(ratingRepository).deleteByTradeId(1L);
        doNothing().when(tradeRepository).deleteById(1L);

        ResponseEntity<?> responseEntity = tradeServiceImpl.deleteTradeById(1L);

        verify(tradeRepository, times(1)).deleteById(1L);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }
    @Test
    public void deleteTradeTest_WhenIdNotExists(){
        when(tradeRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> responseEntity = tradeServiceImpl.deleteTradeById(1L);

        verify(tradeRepository, times(0)).deleteById(1L);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    public void getTradeByIdTest_WhenIdExists(){
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(tradeDAO));
        when(tradeMapStruct.TradeDAOToTrade(tradeDAO)).thenReturn(trade);

        ResponseEntity<Trade> responseEntity = tradeServiceImpl.getTradeById(1L);

        verify(tradeRepository, times(1)).findById(1L);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(trade, responseEntity.getBody());
    }
    @Test
    public void getTradeByIdTest_WhenIdNotExists(){
        when(tradeRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Trade> responseEntity = tradeServiceImpl.getTradeById(1L);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
    @Test
    public void getTradeListTest_ListNotEmpty(){
        List<TradeDAO> tradeDAOList = new ArrayList<>();
        tradeDAOList.add(tradeDAO);
        List<Trade> tradeList = new ArrayList<>();
        tradeList.add(trade);
        when(tradeRepository.findAll()).thenReturn(tradeDAOList);
        when(tradeMapStruct.TradeDAOToTrade(tradeDAO)).thenReturn(trade);

        ResponseEntity<List<Trade>> responseEntity = tradeServiceImpl.getTradeList();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(tradeList,responseEntity.getBody());
    }
    @Test
    public void getTradeListTest_ListEmpty(){
        List<TradeDAO> tradeDAOList = new ArrayList<>();
        when(tradeRepository.findAll()).thenReturn(tradeDAOList);

        ResponseEntity<List<Trade>> responseEntity = tradeServiceImpl.getTradeList();

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals(null,responseEntity.getBody());
    }

    @Test
    public void updateTradeTest_WhenTradeExist(){
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(tradeDAO));
        when(tradeMapStruct.TradeDAOToTrade(tradeDAO)).thenReturn(trade);
        when(tradeMapStruct.TradeToTradeDAO(trade)).thenReturn(tradeDAO);
        when(tradeRepository.save(tradeDAO)).thenReturn(tradeDAO);


        ResponseEntity<Trade> responseEntity = tradeServiceImpl.updateTrade(1L,trade);

        verify(tradeRepository, times(1)).save(tradeDAO);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(trade, responseEntity.getBody());
        assertEquals(trade.getTradeStatus(),TradeStatus.UNDERGOING);
    }
    @Test
    public void updateTradeTest_WhenTradeNotExist(){
        when(tradeRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Trade> responseEntity = tradeServiceImpl.updateTrade(1L,trade);

        verify(tradeRepository, times(0)).save(tradeDAO);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
    @Test
    public void updateTradeTest_WhenTradeStatusNotUndergoing() {
        when(tradeRepository.findById(1L)).thenReturn(Optional.of(tradeDAO));
        tradeDAO.setTradeStatus(TradeStatus.COMPLETED);

        ResponseEntity<Trade> responseEntity = tradeServiceImpl.updateTrade(1L, trade);

        verify(tradeRepository, times(0)).save(tradeDAO);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }


}
