package com.Gintaras.tcgtrading.trade_service.bussiness.service.impl;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RatingDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RatingRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.RatingService;
import com.Gintaras.tcgtrading.trade_service.mapper.RatingMapStruct;
import com.Gintaras.tcgtrading.trade_service.model.Rating;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RatingServiceImpl implements RatingService {

    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    RatingMapStruct rattingMapper;
    @Override
    public Rating saveRating(Rating rating){
        RatingDAO ratingDao = ratingRepository.save(rattingMapper.RatingToRatingDAO(rating));
        log.info("New Rating is saved: {}", rating);
        return rattingMapper.RatingDAOToRating(ratingDao);
    }
    @Override
    public void deleteRatingById(Long id){
        ratingRepository.deleteById(id);
        log.info("Rating with id {} has been deleted", id);
    }

    @Override
    public Optional<Rating> getRatingById(Long id){
        Optional<Rating> rating = ratingRepository.findById(id).map(rattingMapper::RatingDAOToRating);
        log.info("Rating with id {} is {}", id, rating.isPresent() ? rating.get() : "not found");
        return rating;
    }

    @Override
    public List<Rating> getRatingList (){
        List<RatingDAO> ratingList = ratingRepository.findAll();
        log.info("Get Rating list. Size is: {}", ratingList::size);
        return ratingList.stream().map(rattingMapper::RatingDAOToRating).collect(Collectors.toList());

    }
}
