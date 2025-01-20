package com.Gintaras.tcgtrading.trade_service.bussiness.service;

import com.Gintaras.tcgtrading.trade_service.model.Rating;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RatingService {

    public ResponseEntity<Rating> saveRating(Rating rating);

    public ResponseEntity<Rating> getRatingById(Long id);

    public ResponseEntity<List<Rating>> getRatingList();

    public ResponseEntity<Void> deleteRatingById(Long id);

    public ResponseEntity<Double> getAverageUserRating(String userId);
}
