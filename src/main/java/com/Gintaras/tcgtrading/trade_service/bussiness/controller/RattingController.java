package com.Gintaras.tcgtrading.trade_service.bussiness.controller;

import com.Gintaras.tcgtrading.trade_service.bussiness.service.RatingService;
import com.Gintaras.tcgtrading.trade_service.model.Rating;
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
@RequestMapping("/api/v1/rating")
public class RattingController {

    @Autowired
    RatingService ratingService;

    @PostMapping
    @ApiOperation(value = "Saves Rating to database",
            notes = "If valid Rating body is provided it is saved in the database",
            response = Rating.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<?> saveRating(@ApiParam(value = "Rating model that we want to save", required = true)
                                @Valid @RequestBody Rating rating, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn(HTMLResponseMessages.HTTP_400);
            return ResponseEntity.badRequest().body(HTMLResponseMessages.HTTP_400);
        }
        Rating savedRating = ratingService.saveRating(rating);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRating);

    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updates Rating in database",
            notes = "If Rating exists with provided Id then it is updated according to provided body",
            response = Rating.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 400, message = HTMLResponseMessages.HTTP_400),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.ACCEPTED)
    ResponseEntity<?> updateRating(@ApiParam(value = "The id of the Rating", required = true)
                                  @PathVariable @NonNull Long id,
                                  @ApiParam(value = "The updating Rating model", required = true)
                                  @Valid @RequestBody Rating rating, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn(HTMLResponseMessages.HTTP_400);
            return ResponseEntity.badRequest().body(HTMLResponseMessages.HTTP_400);
        }
        if (!Objects.equals(rating.getId(), id)) {
            log.warn("Provided Rating ids are not equal: {}!={}", id, rating.getId());
            return ResponseEntity.badRequest().body("Unsuccessful request responds with this code." +
                    "Passed data has errors - provided Rating ids are not equal.");
        }
        Optional<Rating> ratingById = ratingService.getRatingById(id);
        if (ratingById.isEmpty()) {
            log.info("Rating with id {} does not exist", id);
            return ResponseEntity.notFound().build();
        }
        Rating updatedRating = ratingService.saveRating(rating);
        log.info("Rating with id {} is updated: {}", id, updatedRating);
        return ResponseEntity.status(HttpStatus.OK).body(updatedRating);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Deletes Rating in database",
            notes = "If Rating exists with provided Id then it is deleted from the database",
            response = Rating.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = HTMLResponseMessages.HTTP_204_WITH_DATA),
            @ApiResponse(code = 401, message = "The request requires user authentication"),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<?> deleteRatingById(@ApiParam(value = "The id of the Rating", required = true)
                                      @PathVariable @NonNull Long id) {
        Optional<Rating> ratingById = ratingService.getRatingById(id);
        if (ratingById.isEmpty()) {
            log.warn("Rating for delete with id {} is not found.", id);
            return ResponseEntity.notFound().build();
        }
        ratingService.deleteRatingById(id);
        log.info("Rating with id {} is deleted: {}", id, ratingById);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @ApiOperation(
            value = "Get a list of all Ratings",
            response = Rating.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)
    })
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Rating>> getAllRatings() {
        List<Rating> foundRatings = ratingService.getRatingList();
        if (foundRatings.isEmpty()) {
            log.warn("Rating list is empty: {}", foundRatings);
            return ResponseEntity.notFound().build();
        } else {
            log.info("Rating list is: {}", foundRatings::size);
            return new ResponseEntity<>(foundRatings, HttpStatus.OK);
        }
    }

    @ApiOperation(
            value = "Get Rating object from database by Id",
            response = Rating.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)
    })
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = "application/json", path = "/{id}")
    public ResponseEntity<?> getRatingById(@ApiParam(value = "The id of the Rating", required = true)
                                          @PathVariable Long id) {
        Optional<Rating> ratingById = ratingService.getRatingById(id);
        if (ratingById.isEmpty()) {
            log.info("Rating with id {} does not exist", id);
            return ResponseEntity.notFound().build();
        } else {
            log.info("Rating with id {} is found: {}", id, ratingById);
            return ResponseEntity.ok(ratingById);
        }
    }
}
