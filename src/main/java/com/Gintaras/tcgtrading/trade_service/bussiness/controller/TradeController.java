package com.Gintaras.tcgtrading.trade_service.bussiness.controller;

import com.Gintaras.tcgtrading.trade_service.bussiness.service.TradeService;
import com.Gintaras.tcgtrading.trade_service.model.Trade;
import com.Gintaras.tcgtrading.trade_service.swagger.HTMLResponseMessages;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@Log4j2
@RequestMapping("/api/v1/trade")
public class TradeController {
    @Autowired
    TradeService tradeService;

    @PostMapping
    @ApiOperation(value = "Saves Trade to database",
            notes = "If valid Trade body is provided it is saved in the database",
            response = Trade.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<?> saveTrade(@ApiParam(value = "Trade model that we want to save", required = true)
                                @Valid @RequestBody Trade trade, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn(HTMLResponseMessages.HTTP_400);
            return ResponseEntity.badRequest().body(HTMLResponseMessages.HTTP_400);
        }
        Trade savedTrade = tradeService.saveTrade(trade).getBody();
        if(savedTrade == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTrade);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updates Trade in database",
            notes = "If Trade exists with provided Id then it is updated according to provided body",
            response = Trade.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<?> updateTrade(@ApiParam(value = "The id of the Trade", required = true)
                                  @PathVariable @NonNull Long id,
                                  @ApiParam(value = "The updating Trade model", required = true)
                                  @Valid @RequestBody Trade trade, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn(HTMLResponseMessages.HTTP_400);
            return ResponseEntity.badRequest().body(HTMLResponseMessages.HTTP_400);
        }
        if (!Objects.equals(trade.getId(), id)) {
            log.warn("Provided Trade ids are not equal: {}!={}", id, trade.getId());
            return ResponseEntity.badRequest().body("Unsuccessful request responds with this code." +
                    "Passed data has errors - provided Trade ids are not equal.");
        }
        ResponseEntity<Trade> updatedTrade = tradeService.updateTrade(id, trade);
        if(updatedTrade.getStatusCode() == HttpStatus.NOT_FOUND) {
            return ResponseEntity.notFound().build();
        }
        log.info("Trade with id {} is updated: {}", id, updatedTrade.getBody());
        return updatedTrade;
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes Trade in database",
            notes = "If Trade exists with provided Id then it is deleted from the database",
            response = Trade.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = HTMLResponseMessages.HTTP_204_WITH_DATA),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<?> deleteTradeById(@ApiParam(value = "The id of the trade", required = true)
                                      @PathVariable @NonNull Long id) {
        ResponseEntity<Trade> tradeById = tradeService.getTradeById(id);
        if (tradeById.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.warn("Trade for delete with id {} is not found.", id);
            return ResponseEntity.notFound().build();
        }
        tradeService.deleteTradeById(id);
        log.info("Trade with id {} is deleted.", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "Get a list of all Trades", response = Trade.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Trade>> getAllTrades() {
        ResponseEntity<List<Trade>> response = tradeService.getTradeList();


        if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
            log.warn("Trade list is empty.");
            return response;
        }

        // Log the size of the trade list
        log.info("Trade list size: {}", response.getBody().size());

        return response;
    }


    @ApiOperation(value = "Get Trade object from database by Id", response = Trade.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = "application/json", path = "/{id}")
    public ResponseEntity<?> getTradeById(@ApiParam(value = "The id of the Trade", required = true)
                                          @PathVariable Long id) {
        ResponseEntity<Trade> tradeById = tradeService.getTradeById(id);
        if (tradeById.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.info("Trade with id {} does not exist", id);
            return ResponseEntity.notFound().build();
        } else {
            log.info("Trade with id {} is found: {}", id, tradeById.getBody());
            return tradeById;
        }
    }

    @PutMapping("complete/{id}")
    @ApiOperation(value = "Marks trade as completed",
            notes = "If trade with provided Id exists it is marked as completed if both users have enough cards",
            response = Trade.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<Trade> completeTrade(@ApiParam(value = "The id of the Trade", required = true)
                                        @PathVariable @NonNull Long id) {

        ResponseEntity<Trade> response = tradeService.completeTrade(id);

        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("Trade with id {} is completed: {}", id, response.getBody());
        } else {
            log.warn("Failed to complete trade with id {}: {}", id, response.getStatusCode());
        }

        return response;
    }
}


