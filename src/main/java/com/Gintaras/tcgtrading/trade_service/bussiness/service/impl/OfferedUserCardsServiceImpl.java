package com.Gintaras.tcgtrading.trade_service.bussiness.service.impl;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.OfferedUserCardsDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.OfferedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.OfferedUserCardsService;
import com.Gintaras.tcgtrading.trade_service.mapper.OfferedUserCardsMapStruct;
import com.Gintaras.tcgtrading.trade_service.model.OfferedUserCards;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class OfferedUserCardsServiceImpl implements OfferedUserCardsService {

    @Autowired
    OfferedCardRepository offeredCardsRepository;
    @Autowired

    OfferedUserCardsMapStruct offeredCardsMapper;
    @Override
    public OfferedUserCards saveOfferedCards(OfferedUserCards offeredUserCards){
        OfferedUserCardsDAO offeredUserCardsDAO = offeredCardsRepository.save(offeredCardsMapper
                .OfferedCardsToOfferedCardsDAO(offeredUserCards));
        log.info("New Offered User Cards are saved: {}", offeredUserCards);
        return offeredCardsMapper.OfferedUserCardsDAOToOfferedUserCards(offeredUserCardsDAO);
    }
    @Override
    public void deleteOfferedCardsById(Long id){
        offeredCardsRepository.deleteById(id);
        log.info("Offered User Cards with id {} have been deleted", id);
    }

    @Override
    public Optional<OfferedUserCards> getOfferedCardsById(Long id){
        Optional<OfferedUserCards> offeredUserCards = offeredCardsRepository.findById(id)
                .map(offeredCardsMapper::OfferedUserCardsDAOToOfferedUserCards);
        log.info("Offered User Cards with id {} are {}", id, offeredUserCards.isPresent() ? offeredUserCards.get() : "not found");
        return offeredUserCards;
    }

    @Override
    public List<OfferedUserCards> getOfferedCards (){
        List<OfferedUserCardsDAO> offeredCardList = offeredCardsRepository.findAll();
        log.info("Get Offered cards list. Size is: {}", offeredCardList::size);
        return offeredCardList.stream().map(offeredCardsMapper::OfferedUserCardsDAOToOfferedUserCards).collect(Collectors.toList());

    }
}
