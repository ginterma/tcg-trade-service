package com.Gintaras.tcgtrading.trade_service.mapper;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RatingDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RequestedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.TradeDAO;
import com.Gintaras.tcgtrading.trade_service.model.Rating;
import com.Gintaras.tcgtrading.trade_service.model.RequestedUserCards;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = TradeMapStruct.class)
public interface RatingMapStruct {

    @Mapping(source = "tradeId", target = "tradeDAO", qualifiedByName = "TradeIdToTradeDAO")
    RatingDAO RatingToRatingDAO (Rating rating);

    @Mapping(source = "tradeDAO.id", target = "tradeId")
    Rating RatingDAOToRating (RatingDAO ratingDAO);

    @Named("TradeIdToTradeDAO")
    default TradeDAO tradeIdToTradeDAO(Long tradeId) {
        if (tradeId == null) {
            return null;
        }
        TradeDAO tradeDAO = new TradeDAO();
        tradeDAO.setId(tradeId);
        return tradeDAO;
    }
}
