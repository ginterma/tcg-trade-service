package com.Gintaras.tcgtrading.trade_service.bussiness.service.impl;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RatingDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RatingRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.TradeRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.RatingService;
import com.Gintaras.tcgtrading.trade_service.mapper.RatingMapStruct;
import com.Gintaras.tcgtrading.trade_service.model.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    RatingMapStruct ratingMapper;

    @Autowired
    TradeRepository tradeRepository;

    @Override
    public ResponseEntity<Rating> saveRating(Rating rating) {
        if (tradeRepository.findById(rating.getTradeId()).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        RatingDAO ratingDao = ratingRepository.save(ratingMapper.RatingToRatingDAO(rating));
        Rating savedRating = ratingMapper.RatingDAOToRating(ratingDao);
        return new ResponseEntity<>(savedRating, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteRatingById(Long id) {
        Optional<RatingDAO> ratingDAO = ratingRepository.findById(id);
        if (ratingDAO.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ratingRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Rating> getRatingById(Long id) {
        Optional<Rating> rating = ratingRepository.findById(id).map(ratingMapper::RatingDAOToRating);
        if (rating.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(rating.get(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Rating>> getRatingList() {
        List<RatingDAO> ratingList = ratingRepository.findAll();
        if (ratingList.isEmpty()) {
            List<Rating> ratings = ratingList.stream().map(ratingMapper::RatingDAOToRating).collect(Collectors.toList());
            return new ResponseEntity<>(ratings, HttpStatus.NO_CONTENT);
        }
        List<Rating> ratings = ratingList.stream().map(ratingMapper::RatingDAOToRating).collect(Collectors.toList());
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Double> getAverageUserRating(String userId) {
        List<RatingDAO> ratingList = ratingRepository.selectByUserId(userId);
        if (ratingList.isEmpty()) {
            return new ResponseEntity<>(0.0, HttpStatus.OK);
        }
        double avgRating = 0;
        for (RatingDAO ratingDAO : ratingList) {
            avgRating += ratingDAO.getRating();
        }
        double average = avgRating / ratingList.size();
        return new ResponseEntity<>(average, HttpStatus.OK);
    }
}
