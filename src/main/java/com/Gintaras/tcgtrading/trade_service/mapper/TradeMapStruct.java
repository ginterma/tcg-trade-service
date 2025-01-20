package com.Gintaras.tcgtrading.trade_service.mapper;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.OfferedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RatingDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RequestedUserCardsDAO;
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



    @Mapping(source = "offeredCardList", target = "offeredCardList", qualifiedByName = "offeredCardIdToDAO")
    @Mapping(source = "requestedCardList", target = "requestedCardList", qualifiedByName = "requestedCardIdToDAO")
    TradeDAO TradeToTradeDAO(Trade trade);

    @Mapping(source = "offeredCardList", target = "offeredCardList", qualifiedByName = "offeredCardDAOToIds")
    @Mapping(source = "requestedCardList", target = "requestedCardList", qualifiedByName = "requestedCardDAOToIds")
    Trade TradeDAOToTrade(TradeDAO tradeDAO);

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
    @Named("offeredCardIdToDAO")
    default List<OfferedUserCardsDAO> offeredCardIdToDAO(List<Long> cardIds) {
        List<OfferedUserCardsDAO> cardsDAOS = new ArrayList<>();
        if (isNotEmpty(cardIds)) {
            cardIds.forEach(cardId -> cardsDAOS.add(new OfferedUserCardsDAO(cardId)));
        }
        return cardsDAOS;
    }

    @Named("requestedCardIdToDAO")
    default List<RequestedUserCardsDAO> requestedCardIdToDAO(List<Long> cardIds) {
        List<RequestedUserCardsDAO> cardsDAOS = new ArrayList<>();
        if (isNotEmpty(cardIds)) {
            cardIds.forEach(cardId -> cardsDAOS.add(new RequestedUserCardsDAO(cardId)));
        }
        return cardsDAOS;
    }

    @Named("offeredCardDAOToIds")
    default List<Long> offeredCardDAOToIds(List<OfferedUserCardsDAO> cardsDAOS) {
        List<Long> cardIds = new ArrayList<>();
        if (isNotEmpty(cardsDAOS)) {
            cardsDAOS.forEach(cardDAO -> cardIds.add(cardDAO.getId()));
        }
        return cardIds;
    }

    @Named("requestedCardDAOToIds")
    default List<Long> requestedCardDAOToIds(List<RequestedUserCardsDAO> cardsDAOS) {
        List<Long> cardIds = new ArrayList<>();
        if (isNotEmpty(cardsDAOS)) {
            cardsDAOS.forEach(cardDAO -> cardIds.add(cardDAO.getId()));
        }
        return cardIds;
    }
}
