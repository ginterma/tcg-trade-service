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
public class RatingController {

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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> saveRating(@ApiParam(value = "Rating model that we want to save", required = true)
                                        @Valid @RequestBody Rating rating, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn(HTMLResponseMessages.HTTP_400);
            return ResponseEntity.badRequest().body(HTMLResponseMessages.HTTP_400);
        }
        ResponseEntity<Rating> savedRatingResponse = ratingService.saveRating(rating);
        return savedRatingResponse;
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
    public ResponseEntity<?> updateRating(@ApiParam(value = "The id of the Rating", required = true)
                                          @PathVariable @NonNull Long id,
                                          @ApiParam(value = "The updating Rating model", required = true)
                                          @Valid @RequestBody Rating rating, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn(HTMLResponseMessages.HTTP_400);
            return ResponseEntity.badRequest().body(HTMLResponseMessages.HTTP_400);
        }
        if (!Objects.equals(rating.getId(), id)) {
            log.warn("Provided Rating ids are not equal: {}!={}", id, rating.getId());
            return ResponseEntity.badRequest().body("Unsuccessful request: provided Rating ids are not equal.");
        }
        ResponseEntity<Rating> ratingByIdResponse = ratingService.getRatingById(id);
        if (ratingByIdResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.info("Rating with id {} does not exist", id);
            return ratingByIdResponse;
        }
        ResponseEntity<Rating> updatedRatingResponse = ratingService.saveRating(rating);
        return updatedRatingResponse;
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
    public ResponseEntity<?> deleteRatingById(@ApiParam(value = "The id of the Rating", required = true)
                                              @PathVariable @NonNull Long id) {
        ResponseEntity<Rating> ratingByIdResponse = ratingService.getRatingById(id);
        if (ratingByIdResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.warn("Rating for delete with id {} is not found.", id);
            return ratingByIdResponse;
        }
        ResponseEntity<Void> deleteResponse = ratingService.deleteRatingById(id);
        return deleteResponse;
    }

    @ApiOperation(value = "Get a list of all Ratings", response = Rating.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Rating>> getAllRatings() {
        ResponseEntity<List<Rating>> ratingsResponse = ratingService.getRatingList();
        return ratingsResponse;
    }

    @ApiOperation(value = "Get Rating object from database by Id", response = Rating.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = "application/json", path = "/{id}")
    public ResponseEntity<?> getRatingById(@ApiParam(value = "The id of the Rating", required = true)
                                           @PathVariable Long id) {
        ResponseEntity<Rating> ratingByIdResponse = ratingService.getRatingById(id);
        if (ratingByIdResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            log.info("Rating with id {} does not exist", id);
            return ratingByIdResponse; // Return ResponseEntity from getRatingById
        }
        log.info("Rating with id {} is found: {}", id, ratingByIdResponse.getBody());
        return ratingByIdResponse;
    }

    @ApiOperation(value = "Get Average rating of user", response = Double.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = HTMLResponseMessages.HTTP_200),
            @ApiResponse(code = 404, message = HTMLResponseMessages.HTTP_404),
            @ApiResponse(code = 500, message = HTMLResponseMessages.HTTP_500)})
    @ResponseStatus(value = HttpStatus.OK)
    @GetMapping(produces = "application/json", path = "/average/{id}")
    public ResponseEntity<?> getAverageRatingById(@ApiParam(value = "The id of the User", required = true)
                                                  @PathVariable String id) {
        ResponseEntity<Double> averageRatingResponse = ratingService.getAverageUserRating(id);
        return averageRatingResponse;
    }
}
