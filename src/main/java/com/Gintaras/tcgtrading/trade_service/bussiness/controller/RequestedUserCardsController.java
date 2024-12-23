package com.Gintaras.tcgtrading.trade_service.bussiness.controller;

import com.Gintaras.tcgtrading.trade_service.bussiness.service.RequestedUserCardsService;
import com.Gintaras.tcgtrading.trade_service.model.RequestedUserCards;
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
@RequestMapping("/api/v1/requested/cards")
public class RequestedUserCardsController {

    @Autowired
    RequestedUserCardsService requestedUserCardsService;

    @PostMapping
    @ApiOperation(value = "Saves Requested Cards to database",
            notes = "If valid Requested Cards body is provided it is saved in the database",
            response = RequestedUserCards.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<?> saveRequestedCards(@ApiParam(value = "Requested Cards model that we want to save", required = true)
                                       @Valid @RequestBody RequestedUserCards requestedUserCards, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn(HTMLResponseMessages.HTTP_400);
            return ResponseEntity.badRequest().body(HTMLResponseMessages.HTTP_400);
        }
        RequestedUserCards savedRequestedCards = requestedUserCardsService.saveRequestedCards(requestedUserCards);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRequestedCards);

    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updates Requested Cards in database",
            notes = "If Requested Cards exists with provided Id then it is updated according to provided body",
            response = RequestedUserCards.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<?> updateRequestedCards(@ApiParam(value = "The id of the Requested Cards", required = true)
                                         @PathVariable @NonNull Long id,
                                         @ApiParam(value = "The updating Requested Cards model", required = true)
                                         @Valid @RequestBody RequestedUserCards requestedUserCards, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn(HTMLResponseMessages.HTTP_400);
            return ResponseEntity.badRequest().body(HTMLResponseMessages.HTTP_400);
        }
        if (!Objects.equals(requestedUserCards.getId(), id)) {
            log.warn("Provided Requested Cards ids are not equal: {}!={}", id, requestedUserCards.getId());
            return ResponseEntity.badRequest().body("Unsuccessful request responds with this code." +
                    "Passed data has errors - provided Requested Cards ids are not equal.");
        }
        Optional<RequestedUserCards> requestedCardsById = requestedUserCardsService.getRequestedCardsById(id);
        if (requestedCardsById.isEmpty()) {
            log.info("Requested Cards with id {} do not exist", id);
            return ResponseEntity.notFound().build();
        }
        RequestedUserCards updatedRequestedCards = requestedUserCardsService.saveRequestedCards(requestedUserCards);
        log.info("Requested Cards with id {} is updated: {}", id, updatedRequestedCards);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRequestedCards);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes Requested Cards in database",
            notes = "If Requested Cards exists with provided Id then it is deleted from the database",
            response = RequestedUserCards.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = HTMLResponseMessages.HTTP_204_WITH_DATA),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<?> deleteRequestedCardsById(@ApiParam(value = "The id of the Requested Cards", required = true)
                                             @PathVariable @NonNull Long id) {
        Optional<RequestedUserCards> requestedCardsById = requestedUserCardsService.getRequestedCardsById(id);
        if (requestedCardsById.isEmpty()) {
            log.warn("Requested Cards for delete with id {} are not found.", id);
            return ResponseEntity.notFound().build();
        }
        requestedUserCardsService.deleteRequestedCardsById(id);
        log.info("Requested Cards with id {} is deleted: {}", id, requestedCardsById);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @ApiOperation(
            value = "Get a list of all Requested Cards",
            response = RequestedUserCards.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)
    })
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<RequestedUserCards>> getAllRequestedCards() {
        List<RequestedUserCards> foundRequestedCards = requestedUserCardsService.getRequestedCards();
        if (foundRequestedCards.isEmpty()) {
            log.warn("Requested Cards list is empty: {}", foundRequestedCards);
            return ResponseEntity.notFound().build();
        } else {
            log.info("Requested Cards list is: {}", foundRequestedCards::size);
            return new ResponseEntity<>(foundRequestedCards, HttpStatus.OK);
        }
    }

    @ApiOperation(
            value = "Get Requested Cards object from database by Id",
            response = RequestedUserCards.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)
    })
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = "application/json", path = "/{id}")
    public ResponseEntity<?> getRequestedCardsById(@ApiParam(value = "The id of the Requested Cards", required = true)
                                                 @PathVariable Long id) {
        Optional<RequestedUserCards> requestedCardsById = requestedUserCardsService.getRequestedCardsById(id);
        if (requestedCardsById.isEmpty()) {
            log.info("Requested Cards with id {} do not exist", id);
            return ResponseEntity.notFound().build();
        } else {
            log.info("Requested Cards with id {} are found: {}", id, requestedCardsById);
            return ResponseEntity.ok(requestedCardsById);
        }
    }
}
