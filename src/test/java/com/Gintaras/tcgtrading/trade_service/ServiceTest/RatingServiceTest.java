package com.Gintaras.tcgtrading.trade_service.ServiceTest;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RatingDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.TradeDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RatingRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.TradeRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.impl.RatingServiceImpl;
import com.Gintaras.tcgtrading.trade_service.mapper.RatingMapStruct;
import com.Gintaras.tcgtrading.trade_service.model.Rating;
import com.Gintaras.tcgtrading.trade_service.model.TradeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {

    @Mock
    private TradeRepository tradeRepository;
    @Mock
    private RatingRepository ratingRepository;
    @Mock
    private RatingMapStruct ratingMapStruct;


    @InjectMocks
    private RatingServiceImpl ratingServiceImpl;

    private Rating rating;
    private RatingDAO ratingDAO;
    private TradeDAO tradeDAO;

    @BeforeEach
    public void setUp() {
        rating = new Rating(1L, 1L, "1", 10);
        ratingDAO = new RatingDAO(1L, null, "1", 10);
        tradeDAO = new TradeDAO(1L, "1L", "2L", new Date(), new Date(System.currentTimeMillis() + 1000000),
                TradeStatus.UNDERGOING, 500.0, 450.0, null, null, null);

        }
    @Test
    public void saveRatingTest_TradeExist(){
            when(tradeRepository.findById(1L)).thenReturn(Optional.of(tradeDAO));
            when(ratingRepository.save(ratingDAO)).thenReturn(ratingDAO);
            when(ratingMapStruct.RatingDAOToRating(ratingDAO)).thenReturn(rating);
            when(ratingMapStruct.RatingToRatingDAO(rating)).thenReturn(ratingDAO);

            ResponseEntity<Rating> responseEntity = ratingServiceImpl.saveRating(rating);

            assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
            assertEquals(rating, responseEntity.getBody());
        }

    @Test
    public void saveRatingTest_NotTradeExist(){
        when(tradeRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Rating> responseEntity = ratingServiceImpl.saveRating(rating);

        assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void deleteRatingTest_RatingExist(){
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(ratingDAO));

        ResponseEntity<Void> responseEntity = ratingServiceImpl.deleteRatingById(rating.getId());

        assertEquals(HttpStatus.NO_CONTENT,responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void deleteRatingTest_RatingNotExist(){
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Void> responseEntity = ratingServiceImpl.deleteRatingById(rating.getId());

        assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    public void getRatingByIdTest_RatingExist(){
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(ratingDAO));
        when(ratingMapStruct.RatingDAOToRating(ratingDAO)).thenReturn(rating);

        ResponseEntity<Rating> responseEntity = ratingServiceImpl.getRatingById(1L);

        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        assertEquals(rating,responseEntity.getBody());

    }

    @Test
    public void getRatingByIdTest_RatingNotExist(){
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Rating> responseEntity = ratingServiceImpl.getRatingById(1L);

        assertEquals(HttpStatus.NOT_FOUND,responseEntity.getStatusCode());
        assertEquals(null,responseEntity.getBody());

    }
    @Test
    public void getRatingListTest_ListNotEmpty(){
        List<Rating> ratingList = new ArrayList<>();
        List<RatingDAO> ratingDAOList = new ArrayList<>();
        ratingList.add(rating);
        ratingDAOList.add(ratingDAO);
        when(ratingRepository.findAll()).thenReturn(ratingDAOList);
        when(ratingMapStruct.RatingDAOToRating(ratingDAO)).thenReturn(rating);

        ResponseEntity<List<Rating>> responseEntity = ratingServiceImpl.getRatingList();

        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        assertEquals(ratingList,responseEntity.getBody());
    }
    @Test
    public void getRatingListTest_ListEmpty(){
        List<Rating> ratingList = new ArrayList<>();
        List<RatingDAO> ratingDAOList = new ArrayList<>();
        when(ratingRepository.findAll()).thenReturn(ratingDAOList);

        ResponseEntity<List<Rating>> responseEntity = ratingServiceImpl.getRatingList();

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        assertEquals(ratingList,responseEntity.getBody());
    }
    @Test
    public void getAverageUserRatingTest_RatingsExist(){
        List<RatingDAO> ratingDAOList = new ArrayList<>();
        ratingDAOList.add(ratingDAO);
        RatingDAO ratingDAO2 = new RatingDAO(1L, null, "1", 8);
        ratingDAOList.add(ratingDAO2);
        when(ratingRepository.selectByUserId("1")).thenReturn(ratingDAOList);

        ResponseEntity<Double> responseEntity = ratingServiceImpl.getAverageUserRating("1");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(9,responseEntity.getBody());
    }

    @Test
    public void getAverageUserRatingTest_RatingsNotExist(){
        List<RatingDAO> ratingDAOList = new ArrayList<>();
        when(ratingRepository.selectByUserId("1")).thenReturn(ratingDAOList);

        ResponseEntity<Double> responseEntity = ratingServiceImpl.getAverageUserRating("1");

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(0,responseEntity.getBody());
    }



}

