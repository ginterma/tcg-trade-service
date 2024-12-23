package com.Gintaras.tcgtrading.trade_service.mapper;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.OfferedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RatingDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.TradeDAO;
import com.Gintaras.tcgtrading.trade_service.model.OfferedUserCards;
import com.Gintaras.tcgtrading.trade_service.model.Trade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

import static org.hibernate.internal.util.collections.CollectionHelper.isNotEmpty;

@Mapper(componentModel = "spring")
public interface TradeMapStruct {



    TradeDAO TradeToTradeDAO (Trade trade);


    Trade TradeDAOToTrade (TradeDAO tradeDAO);

    default List<RatingDAO> ratingIdsToRatingDAOS(List<Long> ratingIds) {
        List<RatingDAO> ratingDAOS = new ArrayList<>();
        if (isNotEmpty(ratingIds)) {
            ratingIds.forEach(
                    ratingId ->
                            ratingDAOS.add(new RatingDAO(ratingId)));
        }
        return ratingDAOS;
    }

    default List<Long> ratingDAOSToRatingIds(List<RatingDAO> ratingDAOS) {
        List<Long> ratingIds = new ArrayList<>();
        if (isNotEmpty(ratingDAOS)) {
            ratingDAOS.forEach(
                    ratingDAO ->
                            ratingIds.add(ratingDAO.getId()));
        }
        return ratingIds;
    }
}
