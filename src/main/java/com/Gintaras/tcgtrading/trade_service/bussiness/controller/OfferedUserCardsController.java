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
    OfferedUserCardsService offeredUserCardsService;

    @PostMapping
    @ApiOperation(value = "Saves Offered Cards to database",
            notes = "If valid Offered Cards body is provided it is saved in the database",
            response = OfferedUserCards.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<?> saveOfferedCards(@ApiParam(value = "Offered Cards model that we want to save", required = true)
                                 @Valid @RequestBody OfferedUserCards offeredUserCards, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn(HTMLResponseMessages.HTTP_400);
            return ResponseEntity.badRequest().body(HTMLResponseMessages.HTTP_400);
        }
        OfferedUserCards savedOfferedCards = offeredUserCardsService.saveOfferedCards(offeredUserCards);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOfferedCards);

    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updates Offered Cards in database",
            notes = "If Offered Cards exists with provided Id then it is updated according to provided body",
            response = OfferedUserCards.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<?> updateOfferedCards(@ApiParam(value = "The id of the Offered Cards", required = true)
                                   @PathVariable @NonNull Long id,
                                   @ApiParam(value = "The updating Offered Cards model", required = true)
                                   @Valid @RequestBody OfferedUserCards offeredUserCards, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn(HTMLResponseMessages.HTTP_400);
            return ResponseEntity.badRequest().body(HTMLResponseMessages.HTTP_400);
        }
        if (!Objects.equals(offeredUserCards.getId(), id)) {
            log.warn("Provided Offered Cards ids are not equal: {}!={}", id, offeredUserCards.getId());
            return ResponseEntity.badRequest().body("Unsuccessful request responds with this code." +
                    "Passed data has errors - provided Offered Cards ids are not equal.");
        }
        Optional<OfferedUserCards> offeredCardsById = offeredUserCardsService.getOfferedCardsById(id);
        if (offeredCardsById.isEmpty()) {
            log.info("Offered Cards with id {} do not exist", id);
            return ResponseEntity.notFound().build();
        }
        OfferedUserCards updatedOfferedCards = offeredUserCardsService.saveOfferedCards(offeredUserCards);
        log.info("Offered Cards with id {} is updated: {}", id, updatedOfferedCards);
        return ResponseEntity.status(HttpStatus.OK).body(updatedOfferedCards);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes Offered Cards in database",
            notes = "If Offered Cards exists with provided Id then it is deleted from the database",
            response = OfferedUserCards.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = HTMLResponseMessages.HTTP_204_WITH_DATA),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<?> deleteOfferedCardsById(@ApiParam(value = "The id of the Offered Cards", required = true)
                                       @PathVariable @NonNull Long id) {
        Optional<OfferedUserCards> offeredCardsById = offeredUserCardsService.getOfferedCardsById(id);
        if (offeredCardsById.isEmpty()) {
            log.warn("Offered Cards for delete with id {} are not found.", id);
            return ResponseEntity.notFound().build();
        }
        offeredUserCardsService.deleteOfferedCardsById(id);
        log.info("Offered Cards with id {} is deleted: {}", id, offeredCardsById);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @ApiOperation(
            value = "Get a list of all Offered Cards",
            response = OfferedUserCards.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)
    })
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<OfferedUserCards>> getAllOfferedCards() {
        List<OfferedUserCards> foundOfferedCards = offeredUserCardsService.getOfferedCards();
        if (foundOfferedCards.isEmpty()) {
            log.warn("Offered Cards list is empty: {}", foundOfferedCards);
            return ResponseEntity.notFound().build();
        } else {
            log.info("Offered Cards list is: {}", foundOfferedCards::size);
            return new ResponseEntity<>(foundOfferedCards, HttpStatus.OK);
        }
    }

    @ApiOperation(
            value = "Get Offered Cards object from database by Id",
            response = OfferedUserCards.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)
    })
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = "application/json", path = "/{id}")
    public ResponseEntity<?> getOfferedCardsById(@ApiParam(value = "The id of the Offered Cards", required = true)
                                           @PathVariable Long id) {
        Optional<OfferedUserCards> offeredCardsById = offeredUserCardsService.getOfferedCardsById(id);
        if (offeredCardsById.isEmpty()) {
            log.info("Offered Cards with id {} do not exist", id);
            return ResponseEntity.notFound().build();
        } else {
            log.info("Offered Cards with id {} are found: {}", id, offeredCardsById);
            return ResponseEntity.ok(offeredCardsById);
        }
    }
}
