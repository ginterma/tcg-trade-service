package com.Gintaras.tcgtrading.trade_service.bussiness.controller;

import com.Gintaras.tcgtrading.trade_service.bussiness.service.OfferedUserCardsService;
import com.Gintaras.tcgtrading.trade_service.model.OfferedUserCards;
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
@RequestMapping("/api/v1/offered/cards")
public class OfferedUserCardsController {

    @Autowired
    private OfferedUserCardsService offeredUserCardsService;

    @PostMapping
    @ApiOperation(value = "Saves Offered Cards to database",
            notes = "If valid Offered Cards body is provided, it is saved in the database",
            response = OfferedUserCards.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = HTMLResponseMessages.HTTP_201),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<?> saveOfferedCards(@ApiParam(value = "Offered Cards model to save", required = true)
                                              @Valid @RequestBody OfferedUserCards offeredUserCards, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Invalid Offered Cards data: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(HTMLResponseMessages.HTTP_400);
        }
        ResponseEntity<OfferedUserCards> savedOfferedCards = offeredUserCardsService.saveOfferedCards(offeredUserCards);
        return savedOfferedCards;
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updates Offered Cards in database",
            notes = "If Offered Cards exists with the provided Id, it is updated according to the provided body",
            response = OfferedUserCards.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<?> updateOfferedCards(@ApiParam(value = "Offered Cards id", required = true)
                                                @PathVariable @NonNull Long id,
                                                @ApiParam(value = "Offered Cards data to update", required = true)
                                                @Valid @RequestBody OfferedUserCards offeredUserCards, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Invalid data for update: {}", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(HTMLResponseMessages.HTTP_400);
        }
        if (!Objects.equals(offeredUserCards.getId(), id)) {
            log.warn("Mismatched Offered Cards ids: {} != {}", id, offeredUserCards.getId());
            return ResponseEntity.badRequest().body("The provided Offered Cards ids do not match.");
        }
        ResponseEntity<OfferedUserCards> updatedOfferedCards = offeredUserCardsService.saveOfferedCards(offeredUserCards);
        return updatedOfferedCards;
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes Offered Cards in database",
            notes = "If Offered Cards exists with the provided Id, it is deleted from the database")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = HTMLResponseMessages.HTTP_204_WITH_DATA),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<?> deleteOfferedCardsById(@ApiParam(value = "Offered Cards id to delete", required = true)
                                                    @PathVariable @NonNull Long id) {
        ResponseEntity<?> response = offeredUserCardsService.deleteOfferedCardsById(id);
        return response;
    }

    @GetMapping
    @ApiOperation(value = "Get a list of all Offered Cards", response = OfferedUserCards.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<List<OfferedUserCards>> getAllOfferedCards() {
        ResponseEntity<List<OfferedUserCards>> response = offeredUserCardsService.getOfferedCards();
        return response;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get Offered Cards by Id", response = OfferedUserCards.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    public ResponseEntity<?> getOfferedCardsById(@ApiParam(value = "Offered Cards id", required = true)
                                                 @PathVariable Long id) {
        ResponseEntity<?> response = offeredUserCardsService.getOfferedCardsById(id);
        return response;
    }
}
