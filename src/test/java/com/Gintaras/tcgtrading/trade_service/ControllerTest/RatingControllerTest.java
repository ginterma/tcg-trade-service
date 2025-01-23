package com.Gintaras.tcgtrading.trade_service.ControllerTest;

import com.Gintaras.tcgtrading.trade_service.bussiness.controller.RatingController;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RatingDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RatingRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.TradeRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.RatingService;
import com.Gintaras.tcgtrading.trade_service.mapper.RatingMapStruct;
import com.Gintaras.tcgtrading.trade_service.model.Rating;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(RatingController.class)
public class RatingControllerTest {

    private final String RATING_URI = "/api/v1/rating";


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    RatingService ratingService;

    @MockitoBean
    private TradeRepository tradeRepository;
    @MockitoBean
    private RatingRepository ratingRepository;
    @MockitoBean
    private RatingMapStruct ratingMapStruct;


    private Rating rating;
    private RatingDAO ratingDAO;

    @BeforeEach
    public void setUp() {
        rating = new Rating(1L, 1L, "1", 10);
        ratingDAO = new RatingDAO(1L, null, "1", 10);
    }

    @Test
    public void addRatingControllerTest_TradeExist() throws Exception {
        when(ratingService.saveRating(rating)).thenReturn(ResponseEntity.ok(rating));
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(ratingDAO));
        when(ratingMapStruct.RatingToRatingDAO(rating)).thenReturn(ratingDAO);
        when(ratingMapStruct.RatingDAOToRating(ratingDAO)).thenReturn(rating);

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .post(RATING_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonString(rating))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tradeId").value(1L))
                .andExpect(jsonPath("$.rating").value(10));

    }

    @Test
    public void addRatingControllerTest_TradeNotExist() throws Exception {
        when(ratingService.saveRating(rating)).thenReturn( ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .post(RATING_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonString(rating))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }
    @Test
    public void addRatingControllerTest_InvalidRating() throws Exception {
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .post(RATING_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonString(null))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateRatingControllerTest_RatingExist() throws Exception {
        when(ratingService.saveRating(rating)).thenReturn( ResponseEntity.status(HttpStatus.CREATED).body(rating));
        when(ratingService.getRatingById(1L)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(rating));
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(ratingDAO));
        when(ratingRepository.save(ratingDAO)).thenReturn(ratingDAO);
        when(ratingMapStruct.RatingDAOToRating(ratingDAO)).thenReturn(rating);

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .put(RATING_URI + "/" + 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonString(rating))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tradeId").value(1L))
                .andExpect(jsonPath("$.rating").value(10));
    }
    @Test
    public void updateRatingControllerTest_RatingNotExist() throws Exception {
        when(ratingService.saveRating(rating)).thenReturn( ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        when(ratingService.getRatingById(1L)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .put(RATING_URI + "/" + 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonString(rating))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    public void updateRatingControllerTest_IdNotMatch() throws Exception {
        when(ratingService.getRatingById(1L)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .put(RATING_URI + "/" + 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonString(rating))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void updateRatingControllerTest_InvalidRating() throws Exception {
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .put(RATING_URI + "/" + 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonString(null))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void getRatingListTest() throws Exception {
        List<Rating> ratingList = new ArrayList<>();
        List<RatingDAO> ratingDAOList = new ArrayList<>();
        ratingDAOList.add(ratingDAO);
        ratingList.add(rating);
        when(ratingService.getRatingList()).thenReturn(ResponseEntity.status(HttpStatus.OK).body(ratingList));
        when(ratingRepository.findAll()).thenReturn(ratingDAOList);
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(RATING_URI))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L));
        }

    @Test
    public void getRatingByIdControllerTest_RatingExist() throws Exception {
        when(ratingService.getRatingById(1L)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(rating));
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(ratingDAO));
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(RATING_URI + "/" + 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonString(rating))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.tradeId").value(1L))
                .andExpect(jsonPath("$.rating").value(10));
    }

    @Test
    public void getRatingByIdControllerTest_RatingNotExist() throws Exception {
        when(ratingService.getRatingById(1L)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(RATING_URI + "/" + 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonString(rating))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    public void getAverageRatingOfUserControllerTest_RatingExist() throws Exception {
        List<RatingDAO> ratingDAOList = new ArrayList<>();
        ratingDAOList.add(ratingDAO);
        RatingDAO ratingDAO2 = new RatingDAO(1L, null, "1", 8);
        ratingDAOList.add(ratingDAO2);
        when(ratingService.getAverageUserRating("1")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(9.0));
        when(ratingRepository.selectByUserId("1")).thenReturn(ratingDAOList);
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(RATING_URI + "/average/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(9.0));
    }
    @Test
    public void getAverageRatingOfUserControllerTest_RatingNotExist() throws Exception {
        List<RatingDAO> ratingDAOList = new ArrayList<>();
        when(ratingService.getAverageUserRating("1")).thenReturn(ResponseEntity.status(HttpStatus.OK).body(0.0));
        when(ratingRepository.selectByUserId("1")).thenReturn(ratingDAOList);
        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get(RATING_URI + "/average/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").value(0.0));
    }

    @Test
    public void deleteRatingControllerTest_RatingExist() throws Exception {
        when(ratingService.getRatingById(1L)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(rating));
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(ratingDAO));

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .delete(RATING_URI + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteRatingControllerTest_RatingNotExist() throws Exception {
        when(ratingService.getRatingById(1L)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        when(ratingRepository.findById(1L)).thenReturn(Optional.empty());

        ResultActions result = this.mockMvc.perform(MockMvcRequestBuilders
                        .delete(RATING_URI + "/1"))
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
