package com.Gintaras.tcgtrading.trade_service.mapper;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.OfferedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.TradeDAO;
import com.Gintaras.tcgtrading.trade_service.model.OfferedUserCards;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = TradeMapStruct.class)
public interface OfferedUserCardsMapStruct {

    @Mapping(source = "tradeId", target = "tradeDAO", qualifiedByName = "TradeIdToTradeDAO")
    OfferedUserCardsDAO OfferedCardsToOfferedCardsDAO (OfferedUserCards offeredUserCards);

    @Mapping(source = "tradeDAO.id", target = "tradeId")
    OfferedUserCards OfferedUserCardsDAOToOfferedUserCards (OfferedUserCardsDAO offeredUserCardsDAO);

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







