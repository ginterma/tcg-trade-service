package com.Gintaras.tcgtrading.trade_service.bussiness.service;

import com.Gintaras.tcgtrading.trade_service.model.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingService {


    public Rating saveRating(Rating rating);

    public Optional<Rating> getRatingById(Long Id);

    public List<Rating> getRatingList();

    public void deleteRatingById(Long id);
}
