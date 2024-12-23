package com.Gintaras.tcgtrading.trade_service.bussiness.service.impl;

import com.Gintaras.tcgtrading.trade_service.bussiness.repository.DAO.TradeDAO;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.OfferedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RatingRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.RequestedCardRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.repository.TradeRepository;
import com.Gintaras.tcgtrading.trade_service.bussiness.service.TradeService;
import com.Gintaras.tcgtrading.trade_service.mapper.TradeMapStruct;
import com.Gintaras.tcgtrading.trade_service.model.Trade;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class TradeServiceImpl implements TradeService {


    @Autowired
    TradeRepository tradeRepository;
    @Autowired
    OfferedCardRepository offeredCardRepository;
    @Autowired
    RequestedCardRepository requestedCardRepository;
    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    TradeMapStruct tradeMapper;

    @Override
    public Trade saveTrade(Trade trade){
        TradeDAO tradeDAO = tradeRepository.save(tradeMapper.TradeToTradeDAO(trade));
        log.info("New Trade is saved: {}", trade);
        return tradeMapper.TradeDAOToTrade(tradeDAO);
    }
    @Override
    @Transactional
    public void deleteTradeById(Long tradeId){
        ratingRepository.deleteByTradeId(tradeId);
        requestedCardRepository.deleteByTradeId(tradeId);
        offeredCardRepository.deleteByTradeId(tradeId);
        tradeRepository.deleteById(tradeId);
        log.info("Trade with id {} has been deleted. Offered and Requested " +
                "Cards Records were also deleted. Trade ratings deleted.", tradeId);
    }

    @Override
    public Optional<Trade> getTradeById(Long id){
        Optional<Trade> trade = tradeRepository.findById(id).map(tradeMapper::TradeDAOToTrade);
        log.info("Trade with id {} is {}", id, trade.isPresent() ? trade.get() : "not found");
        return trade;
    }

    @Override
    public List<Trade> getTradeList (){
        List<TradeDAO> tradeList = tradeRepository.findAll();
        log.info("Get trade list. Size is: {}", tradeList::size);
        return tradeList.stream().map(tradeMapper::TradeDAOToTrade).collect(Collectors.toList());

    }
}
