package com.Gintaras.tcgtrading.trade_service.bussiness.service.impl;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.RequestedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RequestedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.RequestedUserCardsService;
import com.Gintaras.tcgtrading.trade_service.mapper.RequestedUserCardsMapStruct;
import com.Gintaras.tcgtrading.trade_service.model.RequestedUserCards;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RequestedUserCardsServiceImpl implements RequestedUserCardsService {

    @Autowired
    RequestedCardRepository requestedCardRepository;

    @Autowired
    RequestedUserCardsMapStruct requestedCardsMapper;

    @Override
    public RequestedUserCards saveRequestedCards(RequestedUserCards requestedUserCards){
        RequestedUserCardsDAO requestedUserCardsDAO = requestedCardRepository.save(requestedCardsMapper
                .RequestedCardsToRequestedCardsDAO(requestedUserCards));
        log.info("New Requested User Cards are saved: {}", requestedUserCards);
        return requestedCardsMapper.RequestedUserCardsDAOToRequestedUserCards(requestedUserCardsDAO);
    }
    @Override
    public void deleteRequestedCardsById(Long id){
        requestedCardRepository.deleteById(id);
        log.info("Requested User Cards with id {} have been deleted", id);
    }

    @Override
    public Optional<RequestedUserCards> getRequestedCardsById(Long id){
        Optional<RequestedUserCards> requestedUserCards = requestedCardRepository.findById(id)
                .map(requestedCardsMapper::RequestedUserCardsDAOToRequestedUserCards);
        log.info("Requested User Cards with id {} are {}", id, requestedUserCards.isPresent() ? requestedUserCards.get() : "not found");
        return requestedUserCards;
    }

    @Override
    public List<RequestedUserCards> getRequestedCards (){
        List<RequestedUserCardsDAO> requestedCardList = requestedCardRepository.findAll();
        log.info("Get Requested cards list. Size is: {}", requestedCardList::size);
        return requestedCardList.stream().map(requestedCardsMapper::RequestedUserCardsDAOToRequestedUserCards).collect(Collectors.toList());

    }
}
